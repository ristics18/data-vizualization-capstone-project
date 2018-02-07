var myApp = angular.module('searchByDate', ['ui.bootstrap', 'chart.js', 'ngDialog', 'cgBusy']);

myApp.controller('searchByDateController', ['$scope', '$http', 'ngDialog', 'anchorSmoothScroll', function ($scope, $http, ngDialog, anchorSmoothScroll) {

    $scope.mainHashtag = "";
    $scope.firstRelation = "";
    $scope.secondRelation = "";
    $scope.thirdRelation = "";
    $scope.fourthRelation = "";
    $scope.fifthRelation = "";
    $scope.sixthRelation = "";
    $scope.arrayOfDates = [];
    $scope.arrayOfNumberOfTweets = [];
    $scope.showTable = false;
    $scope.showRelationChart = false;
    $scope.delay = 0;
    $scope.minDuration = 0;
    $scope.message = 'LOADING DATA...';
    $scope.backdrop = true;
    $scope.promiseDateDiagram = null;
    $scope.promiseTable = null;
    $scope.promiseRelationChart = null;
    var railwayChart;
    var dataForRelationChart = [
        {
            "id": "01",
            "label": $scope.mainHashtag,
            "color": "#FF0000",
            "tooltext": 0,
            "x": "50",
            "y": "55",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "02",
            "label": $scope.firstRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "25",
            "y": "85",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "03",
            "label": $scope.secondRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "75",
            "y": "85",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "04",
            "label": $scope.thirdRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "15",
            "y": "55",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "05",
            "label": $scope.fourthRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "85",
            "y": "55",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "06",
            "label": $scope.fifthRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "25",
            "y": "25",
            "radius": "50",
            "shape": "circle"
        },
        {
            "id": "07",
            "label": $scope.sixthRelation,
            "color": "#808080",
            "tooltext": 0,
            "x": "75",
            "y": "25",
            "radius": "50",
            "shape": "circle"
        }
    ];
    var connectorsForRelationChart = [
        {
            "color": "#ffffff",
            "stdThickness": "20",
            "connector": [
                {
                    "from": "01",
                    "to": "02",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                },
                {
                    "from": "01",
                    "to": "03",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                },
                {
                    "from": "01",
                    "to": "04",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                },
                {
                    "from": "01",
                    "to": "05",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                },
                {
                    "from": "01",
                    "to": "06",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                },
                {
                    "from": "01",
                    "to": "07",
                    "color": "#000000",
                    "arrowatstart": "0",
                    "arrowatend": "0"
                }
            ]
        }
    ];
    var relatedChartDataSource = {
        "chart": {
            "caption": "Related hashtags - Click on a node to get more info",
            "xaxisminvalue": "0",
            "xaxismaxvalue": "100",
            "yaxisminvalue": "0",
            "yaxismaxvalue": "100",
            "is3d": "0",
            "viewmode": "1",
            "showFormBtn": '0',
            "showRestoreBtn": '0',
            "showplotborder": "0",
            "theme": "fint",
            "showcanvasborder": "1",
            "canvasborderalpha": "20"
        },
        "dataset": [
            {
                "data": dataForRelationChart
            }
        ],
        "connectors": connectorsForRelationChart
    };

    /**
     * Diagram
     */
    $scope.labels = $scope.arrayOfDates;
    $scope.series = ['Count'];
    $scope.data = [$scope.arrayOfNumberOfTweets];
    $scope.chartColors = ["#ffffff"];
    $scope.chartClick = function (point) {
        $scope.showTable = false;
        $scope.showRelationChart = false;
        if (point.length === 0) {
            ngDialog.open({
                template: '<p>Please click on a specific point on a graph!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if ($scope.arrayOfNumberOfTweets[point[0]._index] === 0) {
            ngDialog.open({
                template: '<p>There are no data for this date!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else {
            $scope.$apply(function () {
                $scope.showTable = true;
            });
            $scope.gotoElement('scrollToTable');
            $scope.pickedDate = $scope.arrayOfDates[point[0]._index];
            var date = {"date": $scope.arrayOfDates[point[0]._index]};
            $scope.promiseTable = $http({
                method: 'POST',
                url: 'getThreeMostDominantTagsForSpecificDate',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: date
            }).then(function (response) {
                if (response.data.result.length === 0) {
                    ngDialog.open({
                        template: '<p>Most dominant tags for this date are not available!</p>',
                        plain: true,
                        className: 'ngdialog-theme-default'
                    });
                    $scope.gotoElement('scrollToDate');
                } else {
                    $scope.hashtagOneName = "#" + response.data.result[0]['hashtagName'];
                    $scope.hashtagOneValue = response.data.result[0]['totalNumber'];

                    $scope.hashtagTwoName = "#" + response.data.result[1]['hashtagName'];
                    $scope.hashtagTwoValue = response.data.result[1]['totalNumber'];

                    $scope.hashtagThreeName = "#" + response.data.result[2]['hashtagName'];
                    $scope.hashtagThreeValue = response.data.result[2]['totalNumber'];
                }
            }, function (response) {
                ngDialog.open({
                    template: '<p>An error occurred, please contact system administrator!</p>',
                    plain: true,
                    className: 'ngdialog-theme-default'
                });
            });
        }
    };

    $scope.datasetOverride = [{yAxisID: 'y-axis-1'}, {yAxisID: 'y-axis-2'}];
    $scope.options = {
        scales: {
            yAxes: [
                {
                    id: 'y-axis-1',
                    type: 'linear',
                    display: true,
                    position: 'left',
                    ticks: {
                        beginAtZero: true
                    }
                }
            ]
        }
    };

    /**
     * Datepicker
     */
    $scope.clear = function () {
        $scope.startDate = null;
        $scope.endDate = null;
    };
    $scope.inlineOptions = {
        minDate: new Date(),
        showWeeks: true
    };
    $scope.dateOptions = {
        formatYear: 'yy',
        maxDate: new Date(2020, 5, 22),
        minDate: new Date(2006, 5, 22),
        startingDay: 1
    };
    $scope.openStart = function () {
        $scope.popup1.opened = true;
    };
    $scope.openEnd = function () {
        $scope.popup2.opened = true;
    };
    $scope.setDate = function (year, month, day) {
        $scope.startDate = new Date(year, month, day);
        $scope.endDate = new Date(year, month, day);
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.popup2 = {
        opened: false
    };

    /**
     * Method for formatting because datepicker does not return the correct date
     * @param number
     * @param flag
     * @returns {*}
     */
    var formatDate = function (number, flag) {
        var result;
        if (number < 10 && flag === "month") {
            var n = number + 1;
            if (n >= 10) {
                result = n;
            } else {
                result = "0" + n;
            }

        } else if (number > 10 && flag === "month") {
            var n = number + 1;
            result = n;
        } else if (number < 10) {
            result = "0" + number;
        } else {
            result = number;
        }
        return result;
    };

    /**
     * Compare 2 dates
     * @param time1
     * @param time2
     * @returns {boolean}
     */
    function compareTime(time1, time2) {
        return new Date(time1) < new Date(time2);
    }

    /**
     * Create array of all dates based on user's date input
     * @param days
     * @returns {Date}
     */
    Date.prototype.addDays = function (days) {
        var dat = new Date(this.valueOf());
        dat.setDate(dat.getDate() + days);
        return dat;
    };

    /**
     * Get dates in between
     * @param startDate
     * @param stopDate
     * @returns {Array}
     */
    function getDatesInBetween(startDate, stopDate) {
        var dateArray = new Array();
        var currentDate = startDate;
        while (currentDate <= stopDate) {
            var currentDay = formatDate(currentDate.getDate(), "day");
            var currentMonth = formatDate(currentDate.getMonth(), "month");
            var formatCurrentDate = currentDate.getFullYear() + "-" + currentMonth + "-" + currentDay;
            dateArray.push(formatCurrentDate);
            currentDate = currentDate.addDays(1);
        }
        return dateArray;
    }

    /**
     * Getting number of tweets for specific dates
     */
    $scope.submitDates = function () {
        $scope.showTable = false;
        $scope.showRelationChart = false;
        if ($scope.startDate === "" || $scope.startDate === undefined) {
            ngDialog.open({
                template: '<p>Start date field cannot be empty!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if ($scope.endDate === "" || $scope.endDate === undefined) {
            ngDialog.open({
                template: '<p>End date field cannot be empty!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if (compareTime($scope.startDate, $scope.endDate) === false) {
            ngDialog.open({
                template: '<p>Start date must be before end date!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else {
            $scope.gotoElement('scrollToLineDiagram');
            var startDay = formatDate($scope.startDate.getDate(), "day");
            var startMonth = formatDate($scope.startDate.getMonth(), "month");
            var formatedStartDate = $scope.startDate.getFullYear() + "-" + startMonth + "-" + startDay;
            var endDay = formatDate($scope.endDate.getDate(), "day");
            var endMonth = formatDate($scope.endDate.getMonth(), "month");
            var formatedEndDate = $scope.endDate.getFullYear() + "-" + endMonth + "-" + endDay;
            var dates = {"startDate": formatedStartDate, "endDate": formatedEndDate};
            var allDates = getDatesInBetween($scope.startDate, $scope.endDate);

            $scope.arrayOfDates.splice(0, $scope.arrayOfDates.length);
            $scope.arrayOfNumberOfTweets.splice(0, $scope.arrayOfNumberOfTweets.length);

            $scope.promiseDateDiagram = $http({
                method: 'POST',
                url: 'getNumberOfTweetsByDate',
                data: dates,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.data.result.length != 0) {
                    loop1:
                        for (var j = 0; j < allDates.length; j++) {
                            var checkDate = allDates[j];
                            loop2:
                                for (var i = 0; i < response.data.result.length; i++) {
                                    var onlyDate = response.data.result[i]['create_date'].substring(0, 10);
                                    if (checkDate === onlyDate) {
                                        $scope.arrayOfDates.push(onlyDate);
                                        $scope.arrayOfNumberOfTweets.push(response.data.result[i]['number_of_tweets']);
                                        break loop2;
                                    } else {
                                        if (i === response.data.result.length - 1) {
                                            $scope.arrayOfDates.push(allDates[j]);
                                            $scope.arrayOfNumberOfTweets.push(0);
                                        } else {
                                            continue loop2;
                                        }
                                    }
                                }
                        }
                }
                if ($scope.arrayOfDates.length < 1) {
                    ngDialog.open({
                        template: '<p>There are currently no data between these 2 dates!</p>',
                        plain: true,
                        className: 'ngdialog-theme-default'
                    });
                }
            }, function (response) {
                ngDialog.open({
                    template: '<p>An error occurred, please contact system administrator!</p>',
                    plain: true,
                    className: 'ngdialog-theme-default'
                });
            });
        }
    };

    FusionCharts.ready(function () {
        railwayChart = new FusionCharts({
            type: 'dragnode',
            renderAt: 'renderDragNodeDiagram',
            width: '800',
            height: '600',
            dataFormat: 'json',
            bgColor: "#eee",
            dataSource: relatedChartDataSource,
            "events": {
                "dataPlotClick": function (eventObj, dataObj) {
                    $scope.openRelationDiagram(dataObj.label);
                }
            }
        });
        railwayChart.render();
    });

    /**
     * Opening relation diagram
     * @param hashtag
     */
    $scope.openRelationDiagram = function (hashtag) {
        var obj = {"hashtag": hashtag};
        $scope.showRelationChart = true;
        $scope.gotoElement('scrollToRelationDiagram');
        $scope.promiseRelationChart = $http({
            method: 'POST',
            url: 'getRelatedHashtags',
            headers: {
                'Content-Type': 'application/json'
            },
            data: obj
        }).then(function (response) {
            if (response.data.result.length === 0) {
                ngDialog.open({
                    template: '<p>There are currently no related tags to this one!</p>',
                    plain: true,
                    className: 'ngdialog-theme-default'
                });
                $scope.gotoElement('scrollToRelationDiagram');
                $scope.showRelationChart = false;
            } else {
                dataForRelationChart[0]['label'] = "#" + response.data.result[0]['hashtagName'];
                dataForRelationChart[1]['label'] = "#" + response.data.result[1]['hashtagName'];
                dataForRelationChart[2]['label'] = "#" + response.data.result[2]['hashtagName'];
                dataForRelationChart[3]['label'] = "#" + response.data.result[3]['hashtagName'];
                dataForRelationChart[4]['label'] = "#" + response.data.result[4]['hashtagName'];
                dataForRelationChart[5]['label'] = "#" + response.data.result[5]['hashtagName'];
                dataForRelationChart[6]['label'] = "#" + response.data.result[6]['hashtagName'];

                dataForRelationChart[0]['tooltext'] = 'Tweets: ' + response.data.result[0]['totalNumber'];
                dataForRelationChart[1]['tooltext'] = 'Tweets: ' + response.data.result[1]['totalNumber'];
                dataForRelationChart[2]['tooltext'] = 'Tweets: ' + response.data.result[2]['totalNumber'];
                dataForRelationChart[3]['tooltext'] = 'Tweets: ' + response.data.result[3]['totalNumber'];
                dataForRelationChart[4]['tooltext'] = 'Tweets: ' + response.data.result[4]['totalNumber'];
                dataForRelationChart[5]['tooltext'] = 'Tweets: ' + response.data.result[5]['totalNumber'];
                dataForRelationChart[6]['tooltext'] = 'Tweets: ' + response.data.result[6]['totalNumber'];

                railwayChart.setChartData(relatedChartDataSource, "json");
                railwayChart.render();
            }
        }, function (response) {
            ngDialog.open({
                template: '<p>An error occurred, please contact system administrator!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        });
    };
}]);