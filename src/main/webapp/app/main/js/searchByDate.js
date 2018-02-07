var myApp = angular.module('searchByDate', ['ui.bootstrap', 'chart.js', 'ngDialog', 'cgBusy']);

myApp.controller('searchByDateController', ['$scope', '$http', 'ngDialog', 'anchorSmoothScroll', function ($scope, $http, ngDialog, anchorSmoothScroll) {

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
    $scope.tableDate = "";

    /**
     * Table
     */
    $scope.labels = $scope.arrayOfDates;
    $scope.series = ['Count'];
    $scope.data = [$scope.arrayOfNumberOfTweets];
    $scope.chartColors = ['#FFFFFF'];
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
            var formatDate = $scope.pickedDate.split("-");
            $scope.tableDate = formatDate[2] + "." + formatDate[1] + "." + formatDate[0];
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
        } else if (number >= 10 && flag === "month") {
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

    /**
     * Opening relation diagram
     * @param hashtag
     */
    $scope.openRelationDiagram = function (hashtag) {
        var obj = {"hashtag": hashtag, "date": $scope.pickedDate};
        $scope.showRelationChart = true;
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
                var resultArray = response.data.result;
                var arrayForRelationshipDiagram = [{
                    key: 1,
                    parent: 1,
                    hashtag: hashtag
                }];
                var keyCount = 2;
                for (var i = 0; i < resultArray.length; i++) {
                    var obj = {
                        key: keyCount,
                        parent: 1,
                        hashtag: "#" + response.data.result[i]['hashtagName'],
                        totalNumber: response.data.result[i]['totalNumber']
                    };
                    arrayForRelationshipDiagram.push(obj);
                    keyCount++;
                }
                var model = go.GraphObject.make(go.TreeModel);
                model.nodeDataArray = arrayForRelationshipDiagram;
                myDiagram.model = model;
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