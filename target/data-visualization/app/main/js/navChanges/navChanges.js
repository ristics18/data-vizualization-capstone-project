jQuery(document).ready(function () {

    $('#carouselHacked').carousel();
    $(document).ready(function () {
        $(document).on("scroll", onScroll);

        //smoothscroll
        $('a[href^="#"]').on('click', function (e) {
            e.preventDefault();
            $(document).off("scroll");

            $('a').each(function () {
                $(this).removeClass('active');
            });
            $(this).addClass('active');

            var target = this.hash,
                menu = target;
            $target = $(target);
            $('html, body').stop().animate({
                'scrollTop': $target.offset().top + 2
            }, 500, 'swing', function () {
                window.location.hash = target;
                $(document).on("scroll", onScroll);
            });
        });
    });

    function onScroll(event) {
        var scrollPos = $(document).scrollTop();
    }

    //this code is for animation nav
    jQuery(window).scroll(function () {
        var windowScrollPosTop = jQuery(window).scrollTop();

        if (windowScrollPosTop >= 150) {
            jQuery(".header").css({"background": "#B193DD"});
            jQuery(".top-header img.logo").css({"margin-top": "-40px", "margin-bottom": "6"});
            jQuery(".navbar-default").css({"margin-top": "-15px"});
            jQuery(".col-xs-5").css({"margin-top": "22px", "margin-bottom": "-23px"});

        }
        else {
            jQuery(".header").css({"background": "transparent"});
            jQuery(".top-header img.logo").css({"margin-top": "-12px", "margin-bottom": "25px"});
            jQuery(".navbar-default").css({"margin-top": "12px", "margin-bottom": "0"});

        }

        var homePositon = $("#home").offset().top;
        var hashtagPosition = $("#scrollToHashtag").offset().top;
        var datePosition = $("#scrollToDate").offset().top;
        var chartsPosition = $("#charts").offset().top;
        var scrollPosition = window.scrollY + 90;

        if (homePositon <= scrollPosition && scrollPosition < hashtagPosition) {
            $("ul li:first-child a").addClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
        } else if (hashtagPosition < scrollPosition && scrollPosition < datePosition) {
            $("ul li:nth-child(2) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
        } else if (datePosition < scrollPosition && scrollPosition < chartsPosition) {
            $("ul li:nth-child(3) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(4) a").removeClass("active");
        } else if (scrollPosition >= chartsPosition) {
            $("ul li:nth-child(4) a").addClass("active");
            $("ul li:first-child a").removeClass("active");
            $("ul li:nth-child(2) a").removeClass("active");
            $("ul li:nth-child(3) a").removeClass("active");
        }

    });
});