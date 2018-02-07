var myApp = angular.module('searchByHashtag', ['ngAnimate', 'ngDialog', 'cgBusy', 'chart.js']);
myApp.config(['ChartJsProvider', function (ChartJsProvider) {
    ChartJsProvider.setOptions({
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    });
}]);
myApp.controller('searchByHashtagController', ['$scope', '$http', 'ngDialog', function ($scope, $http, ngDialog) {

    $scope.barChartLabels = [];
    $scope.barChartData = [[]];
    $scope.barChartSeries = ['Count'];
    $scope.barChartColors = ['#42b3e5'];
    $scope.noHashtagsEntered = true;

    if ($scope.barChartLabels.length < 1 && $scope.barChartData[0].length < 1) {
        $scope.enterHashtagsMsg = "";
    }

    $scope.addValueToTextField = function () {
        $scope.hashtagValue = "#";
    };

    $scope.removeHashtagValue = function () {
        $scope.hashtagValue = "";
    };

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
            $scope.promiseBarDiagram = null;
            var jsonData = {"hashtag": $scope.hashtagValue};

            $scope.promiseBarDiagram = $http({
                method: 'POST',
                url: 'getNumberOfTweetsByHashtag',
                data: jsonData,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                $scope.noHashtagsEntered = false;
                $scope.barChartData[0].push(response.data.result);
                $scope.barChartLabels.push(jsonData.hashtag);
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