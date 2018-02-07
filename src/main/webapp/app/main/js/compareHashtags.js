var myApp = angular.module("compareHashtags", ['ngDialog']);

myApp.controller('compareHashtagsController', ['$scope', '$http', 'ngDialog', function ($scope, $http, ngDialog) {
    $scope.hashtagsArray = [{'hashtagName': ''}];
    $scope.showAddButton = true;
    $scope.showRemoveButton = false;
    $scope.arrayCorrelation = ["Pearson", "Spearman"];
    $scope.selectedCorrelation = "";
    $scope.selectedCorrelationSample = "";
    $scope.selectedCorrelationSampleArray = ["2", "3", "4"];
    $scope.delay = 0;
    $scope.minDuration = 0;
    $scope.message = 'LOADING DATA...';
    $scope.backdrop = true;
    $scope.promiseComparingHashtags = null;
    $scope.showCompareHashtagsResults = false;
    $scope.gaugeShown = false;
    $scope.notOpened = true;
    $scope.opened = false;
    // compare hashtags
    $scope.doesNotExistArray = [];
    $scope.correlationArray = [];
    $scope.totalNumberPerHashtagArray = [];
    $scope.languagesArray = [];
    $scope.mostlyTweetedArray = [];
    $scope.retweetsArray = [];
    $scope.tweetsWithLinksArray = [];
    $scope.isDisabled = true;

    var gaugeDiagram = AmCharts.makeChart("gaugeDiagram", {
        "theme": "light",
        "type": "gauge",
        "axes": [{
            "topTextFontSize": 15,
            "topTextYOffset": 70,
            "axisColor": "#31d6ea",
            "axisThickness": 1,
            "endValue": 1,
            "startValue": -1,
            "gridInside": true,
            "inside": true,
            "radius": "50%",
            "valueInterval": 0.5,
            "tickColor": "#67b7dc",
            "startAngle": -90,
            "endAngle": 90,
            "unit": "",
            "bandOutlineAlpha": 0,
            "bands": [{
                "color": "#0080ff",
                "endValue": 1,
                "innerRadius": "105%",
                "radius": "170%",
                "gradientRatio": [0.5, 0, -0.5],
                "startValue": -1
            }, {
                "endValue": 1,
                "innerRadius": "105%",
                "radius": "170%",
                "gradientRatio": [0.5, 0, -0.5],
                "startValue": -1,
                "color": "#FF0000"
            }]
        }],
        "arrows": [{
            "alpha": 1,
            "innerRadius": "35%",
            "nailRadius": 0,
            "radius": "170%",
            "value": 0
        }]
    });

    $scope.addNewField = function () {
        var obj = {
            'hashtagName': ''
        };
        $scope.hashtagsArray.push(obj);
        if ($scope.hashtagsArray.length > 1) {
            $scope.showRemoveButton = true;
        }
    };

    $scope.removeField = function (indexValue) {
        if ($scope.hashtagsArray.length === 1) {
            ngDialog.open({
                template: '<p>You cannot delete all fields!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
            $scope.showAddButton = true;
            $scope.showRemoveButton = false;
        } else {
            if (indexValue > -1) {
                $scope.hashtagsArray.splice(indexValue, 1);
            }
        }
    };

    $scope.compareHashtags = function () {
        $scope.doesNotExistArray = [];
        $scope.correlationArray = [];
        $scope.totalNumberPerHashtagArray = [];
        $scope.languagesArray = [];
        $scope.mostlyTweetedArray = [];
        $scope.retweetsArray = [];
        $scope.tweetsWithLinksArray = [];
        $scope.notOpened = true;
        $scope.opened = false;
        $scope.showCompareHashtagsResults = false;
        if ($scope.hashtagsArray.length < 2) {
            ngDialog.open({
                template: '<p>Please enter at least 2 hashtags!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if ($scope.selectedCorrelation === "" || $scope.selectedCorrelation === undefined) {
            ngDialog.open({
                template: '<p>Please select correlation!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if ($scope.selectedCorrelationSample === "" || $scope.selectedCorrelationSample === undefined) {
            ngDialog.open({
                template: '<p>Please select correlation sample!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else {
            var check = checkHashtagValues();
            if (check === true) {
                if($scope.selectedCorrelationSample === "3" || $scope.selectedCorrelationSample === "4"){
                    $scope.message = "LOADING DATA... THIS MAY TAKE A WHILE";
                } else {
					$scope.message = "LOADING DATA...";
				}
                var obj = {
                    "hashtagsArray": $scope.hashtagsArray,
                    "correlation": $scope.selectedCorrelation,
                    "correlationSample": $scope.selectedCorrelationSample
                };
                $scope.promiseComparingHashtags = $http({
                    method: 'POST',
                    url: 'compareHashtags',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    data: obj
                }).then(function (response) {
                    $scope.showCompareHashtagsResults = true;
                    for (var i = 0; i < response.data.result.length; i++) {
                        var obj = response.data.result[i];
                        if (obj.doesNotExist != undefined && obj.doesNotExist.length > 0) {
                            $scope.doesNotExistArray = obj.doesNotExist;
                        } else if (obj.correlation != undefined && obj.correlation.length > 0) {
                            $scope.correlationArray = obj.correlation;
                        } else if (obj.totalNumberPerHashtag != undefined && obj.totalNumberPerHashtag.length > 0) {
                            $scope.totalNumberPerHashtagArray = obj.totalNumberPerHashtag;
                        } else if (obj.languages != undefined && obj.languages.length > 0) {
                            $scope.languagesArray = obj.languages;
                        } else if (obj.mostlyTweeted != undefined && obj.mostlyTweeted.length > 0) {
                            $scope.mostlyTweetedArray = obj.mostlyTweeted;
                        } else if (obj.retweets != undefined && obj.retweets.length > 0) {
                            $scope.retweetsArray = obj.retweets;
                        } else if (obj.tweetsWithLinks != undefined && obj.tweetsWithLinks.length > 0) {
                            $scope.tweetsWithLinksArray = obj.tweetsWithLinks;
                        }
                    }
                }, function (response) {
                    ngDialog.open({
                        template: '<p>An error occurred, please contact system administrator!</p>',
                        plain: true,
                        className: 'ngdialog-theme-default'
                    });
                });
            }
        }
    };

    var checkHashtagValues = function () {
        for (var i = 0; i < $scope.hashtagsArray.length; i++) {
            if ($scope.hashtagsArray[i].hashtagName === "#" || $scope.hashtagsArray[i].hashtagName.length === 0) {
                ngDialog.open({
                    template: '<p>Please fill out all fields!</p>',
                    plain: true,
                    className: 'ngdialog-theme-default'
                });
                return false;
            } else {
                if ($scope.hashtagsArray[i].hashtagName[0] != "#") {
                    $scope.hashtagsArray[i].hashtagName = "#" + $scope.hashtagsArray[i].hashtagName;
                }
            }
        }
        return true;
    };

    $scope.showGaugeDiagram = function (value, index) {
        $scope.isDisabled = !$scope.isDisabled;
        value = value.toFixed(2);
        gaugeDiagram.write("showGaugeHere-" + index);
        var strength = "";
        if (-0.30 < value && value < 0.30) {
            gaugeDiagram.axes[0].bands[1].color = "#FF0000";
            strength = "Not linear";
        } else if (0.30 < value && value <= 0.50) {
            gaugeDiagram.axes[0].bands[1].color = "#3cd3a3";
            strength = "Poor";
        } else if (0.50 < value && value <= 0.70) {
            gaugeDiagram.axes[0].bands[1].color = "#3cd3a3";
            strength = "Decent";
        } else if (0.70 < value && value <= 0.90) {
            gaugeDiagram.axes[0].bands[1].color = "#3cd3a3";
            strength = "Strong";
        } else if (0.90 < value && value <= 1) {
            gaugeDiagram.axes[0].bands[1].color = "#3cd3a3";
            strength = "Very strong";
        } else if (-0.30 < value && value <= -0.50) {
            gaugeDiagram.axes[0].bands[1].color = "#FF0000";
            strength = "Poor negative";
        } else if (-0.50 < value && value <= -0.70) {
            gaugeDiagram.axes[0].bands[1].color = "#FF0000";
            strength = "Decent negative";
        } else if (-0.70 < value && value <= -0.90) {
            gaugeDiagram.axes[0].bands[1].color = "#FF0000";
            strength = "Strong negative";
        } else if (-0.90 < value && value <= -1) {
            gaugeDiagram.axes[0].bands[1].color = "#FF0000";
            strength = "Very strong negative";
        }
        gaugeDiagram.arrows[0].setValue(value);
        gaugeDiagram.axes[0].setTopText(value + " " + strength);
        gaugeDiagram.axes[0].bands[1].setEndValue(value);
        $scope.opened = !$scope.opened;
        $scope.notOpened = !$scope.notOpened;
    };

    $scope.check = function (value) {
        if (value === true) {
            return false;
        } else {
            return true;
        }
    };
}]);

