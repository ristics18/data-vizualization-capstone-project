var myApp = angular.module("app", ["searchByHashtag", "searchByDate", "pieChart", "twitterMap", "compareHashtags", "totalNumberOfData"]);

myApp.controller('MainController', ['$scope', '$http', '$location', 'anchorSmoothScroll', function ($scope, $http, $location, anchorSmoothScroll) {
    $scope.gotoElement = function (eID) {
        $location.path('/' + eID);
        anchorSmoothScroll.scrollTo(eID);
        if (eID === 'home') {
            $("ul li:first-child a").addClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
            $("ul li:nth-child(5) a").removeClass("active");
            $("ul li:nth-child(6) a").removeClass("active");
        } else if (eID === 'scrollToHashtag') {
            $("ul li:nth-child(2) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
            $("ul li:nth-child(5) a").removeClass("active");
            $("ul li:nth-child(6) a").removeClass("active");
        } else if (eID === 'scrollToDate') {
            $("ul li:nth-child(3) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
            $("ul li:nth-child(5) a").removeClass("active");
            $("ul li:nth-child(6) a").removeClass("active");
        } else if (eID === 'charts') {
            $("ul li:nth-child(4) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(5) a").removeClass("active");
            $("ul li:nth-child(6) a").removeClass("active");
        } else if (eID === 'scrollToMap') {
            $("ul li:nth-child(5) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
            $("ul li:nth-child(6) a").removeClass("active");
        } else if (eID === 'scrollToCompareHashtags') {
            $("ul li:nth-child(6) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
            $("ul li:nth-child(5) a").removeClass("active");
        }
    };
}]);

myApp.service('anchorSmoothScroll', function () {

    this.scrollTo = function (eID) {
        var startY = currentYPosition();
        var stopY = elmYPosition(eID);
        var distance = stopY > startY ? stopY - startY : startY - stopY;
        if (distance < 100) {
            scrollTo(0, stopY);
            return;
        }

        var speed = Math.round(distance / 100);
        if (speed >= 20) speed = 20;
        var step = Math.round(distance / 25);
        var leapY = stopY > startY ? startY + step : startY - step;
        var timer = 0;
        if (stopY > startY) {
            for (var i = startY; i < stopY; i += step) {
                setTimeout("window.scrollTo(0, " + leapY + ")", timer * speed);
                leapY += step;
                if (leapY > stopY) leapY = stopY;
                timer++;
            }
            return;
        }
        for (var i = startY; i > stopY; i -= step) {
            setTimeout("window.scrollTo(0, " + leapY + ")", timer * speed);
            leapY -= step;
            if (leapY < stopY) leapY = stopY;
            timer++;
        }

        function currentYPosition() {
            if (self.pageYOffset) return self.pageYOffset;
            if (document.documentElement && document.documentElement.scrollTop)
                return document.documentElement.scrollTop;
            if (document.body.scrollTop) return document.body.scrollTop;
            return 0;
        }

        function elmYPosition(eID) {
            var elm = document.getElementById(eID);
            var y = elm.offsetTop;
            var node = elm;
            while (node.offsetParent && node.offsetParent != document.body) {
                node = node.offsetParent;
                y += node.offsetTop;
            }
            return y - 90;
        }
    };
});

