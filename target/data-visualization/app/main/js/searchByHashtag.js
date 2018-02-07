var myApp = angular.module('searchByHashtag', ['ngAnimate', 'ngDialog', 'cgBusy']);

myApp.controller('searchByHashtagController', ['$scope', '$http', 'ngDialog', 'anchorSmoothScroll', function ($scope, $http, ngDialog, anchorSmoothScroll) {

    var analysisChart;
    var labelForDiagram = [{"label": "#example"}];
    var totalNumberForDiagram = [{"value": "5000"}];
    var diagramDataSource = {
        "chart": {
            "showvalues": "0",
            "caption": "Number of tweets with specific hashtag",
            "numberprefix": "",
            "xaxisname": "#hashtags",
            "yaxisname": "Count",
            "showBorder": "0",
            "paletteColors": "#0075c2,#1aaf5d,#f2c500",
            "bgColor": "#ffffff",
            "canvasBgColor": "#ffffff",
            "captionFontSize": "14",
            "subcaptionFontSize": "14",
            "subcaptionFontBold": "0",
            "divlineColor": "#999999",
            "divLineIsDashed": "1",
            "divLineDashLen": "1",
            "divLineGapLen": "1",
            "toolTipColor": "#ffffff",
            "toolTipBorderThickness": "0",
            "toolTipBgColor": "#000000",
            "toolTipBgAlpha": "80",
            "toolTipBorderRadius": "2",
            "toolTipPadding": "5",
            "legendBgColor": "#ffffff",
            "legendBorderAlpha": '0',
            "legendShadow": '0',
            "legendItemFontSize": '10',
            "legendItemFontColor": '#666666'
        },
        "categories": [
            {
                "category": labelForDiagram
            }
        ],
        "dataset": [
            {
                "seriesname": "Total number",
                "data": totalNumberForDiagram
            }
        ]
    };

    $scope.addValueToTextField = function () {
        $scope.hashtagValue = "#";
    };

    $scope.removeHashtagValue = function () {
        $scope.hashtagValue = "";
    };

    FusionCharts.ready(function () {
        analysisChart = new FusionCharts({
            type: 'stackedColumn3DLine',
            renderAt: 'renderBarDiagram',
            width: '1200',
            height: '600',
            dataFormat: 'json',
            dataSource: diagramDataSource
        }).render();
    });

    $scope.hashtagEntered = function () {
        if ($scope.hashtagValue[0] != "#") {
            ngDialog.open({
                template: '<p>Hashtag field must start with #!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else if ($scope.hashtagValue == "#") {
            ngDialog.open({
                template: '<p>Hashtag field must contain other characters besides #!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        } else {
            $scope.delay = 0;
            $scope.minDuration = 0;
            $scope.message = 'LOADING DATA...';
            $scope.backdrop = true;
            $scope.promise = null;
            var jsonData = {"hashtag": $scope.hashtagValue};
            $scope.gotoElement('renderBarDiagram');

            $scope.promise = $http({
                method: 'POST',
                url: 'getNumberOfTweetsByHashtag',
                data: jsonData,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                labelForDiagram.push({"label": jsonData.hashtag});
                totalNumberForDiagram.push({"value": response.data.result});
                analysisChart.setChartData(diagramDataSource, "json");
                analysisChart.render();
                $scope.hashtagValue = "";
            }, function (response) {
                ngDialog.open({
                    template: '<p>An error occurred, please contact system administrator!</p>',
                    plain: true,
                    className: 'ngdialog-theme-default'
                });
            });
        }
    };
}]);