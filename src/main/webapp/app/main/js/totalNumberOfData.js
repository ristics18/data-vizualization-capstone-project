var myApp = angular.module('totalNumberOfData', ['chart.js', 'ngDialog', 'cgBusy']);

myApp.controller('totalNumberOfDataController', ['$scope', '$http', 'ngDialog', function ($scope, $http, ngDialog) {

    $scope.delay = 0;
    $scope.minDuration = 0;
    $scope.message = 'LOADING DATA...';
    $scope.backdrop = true;
    $scope.promise = null;
    $scope.totalNumberOfData = 0;

    $scope.promiseTotalNumberOfData = $http({
        method: 'GET',
        url: 'getTotalNumberOfData',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(function (response) {
        $scope.totalNumberOfData = response.data.result.totalNumberOfData;
    }, function (response) {
        ngDialog.open({
            template: '<p>An error occurred, please contact system administrator!</p>',
            plain: true,
            className: 'ngdialog-theme-default'
        });
    });

}]);