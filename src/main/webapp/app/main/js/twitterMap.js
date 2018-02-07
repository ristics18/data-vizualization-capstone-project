var myApp = angular.module('twitterMap', ['ngDialog', 'cgBusy']);

myApp.controller('twitterMapController', ['$scope', '$http', 'ngDialog', function ($scope, $http, ngDialog) {

    $scope.delay = 0;
    $scope.minDuration = 0;
    $scope.message = 'LOADING MAP...';
    $scope.backdrop = true;
    $scope.promise = null;
    $scope.showLoadMapButton = true;
    $scope.locationTweetsValue = 0;
    $scope.locationTweetsValueShow = false;
    $scope.showMap = false;
    var twitterLocationValues = {};

    var twitterMapConfig = AmCharts.makeChart("twitterLocationMap", {
        "type": "map",
        "theme": "light",
        "dataProvider": {
            "map": "worldLow",
            "getAreasFromMap": true
        },
        "areasSettings": {
            "autoZoom": true
        },
        "export": {
            "enabled": false,
            "position": "bottom-right"
        }
    });
    twitterMapConfig.addListener("rollOverMapObject", function (event) {
        var countryName = event.mapObject.title;
        $scope.$apply(function () {
            $scope.locationTweetsValueShow = true;
            var setValue = twitterLocationValues[countryName];
            if (setValue === undefined || setValue === "") {
                $scope.locationTweetsValue = 0;
            } else {
                $scope.locationTweetsValue = setValue;
            }
        });
    });
    twitterMapConfig.addListener("rollOutMapObject", function (event) {
        $scope.$apply(function () {
            $scope.locationTweetsValueShow = false;
            $scope.locationTweetsValue = 0;
        });
    });

    $scope.loadMap = function () {
        $scope.showLoadMapButton = false;
        $scope.showMap = true;
        $scope.promise = $http({
            method: 'GET',
            url: 'getUserLocations',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            twitterLocationValues = response.data.result;
            $scope.showMap = true;
        }, function (response) {
            ngDialog.open({
                template: '<p>An error occurred, please contact system administrator!</p>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        });
    };
}]);