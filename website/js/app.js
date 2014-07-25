/** Create app with external dependencies. */
var theApp = angular.module('theApp', ['ngRoute']);

/** Configure navigation routes */
theApp.config(['$routeProvider',
    function ($routeProvider, $log, $rootScope) {
        $routeProvider
            .when('/overview', { templateUrl: 'views/overview.html', controller: 'ControllerDoNothing'
            }).when('/documentation', { templateUrl: 'views/documentation.html', controller: 'ControllerDoNothing'
            }).when('/start', { templateUrl: 'views/start.html', controller: 'ControllerDoNothing'
            }).when('/support', { templateUrl: 'views/support.html', controller: 'ControllerDoNothing'
            }).when('/elements', { templateUrl: 'views/elements.html', controller: 'ControllerDoNothing'
//            }).when('/elements/:name', { templateUrl:
//                function(params){
//                    return 'views/elements/' + params.name+ '.html';
//                }, controller: 'ControllerDoNothing'
            }).otherwise({ redirectTo: '/overview'});
    }
]);

/** With Angular 1.1.5 and above, debug logging can be turned off. */
theApp.config(function ($logProvider) {
    $logProvider.debugEnabled(true);
});

/** Do-Nothing controller. */
theApp.controller('ControllerDoNothing', function ($scope, $location, $anchorScroll, $log) {

    /** See http://stackoverflow.com/questions/14712223/how-to-handle-anchor-hash-linking-in-angularjs */
    $scope.scrollTo = function(id) {
//        $log.debug("Scrolling to " + id);
        $location.hash(id);
        $anchorScroll();
    }

});

/**
 * Controller for the navbar. This highlights the currently-active tab.
 */
theApp.controller('ControllerNavBar', function ($scope, $rootScope, $location, $log) {
    // Controller magic

    /**
     * Route change interceptor that captures the URL to which we've just
     * navigated.
     */
    $rootScope.$on("$routeChangeSuccess", function (event, current, previous) {

        // We've routed to a new page, so activate the corresponding tab.
        activateTabBasedOnUri($scope);
    });

    /**
     * Activate the appropriate tab, based on the first part of the given URI.
     * Examples:
     * 1) if URI is "/home" activate the "home" tab.
     * 2) if URI is "/support/1" activate the "support" tab.
     * 3) if URI is "/customer/domestic/internal/2389" activate the "customer" tab.
     *
     * @param $scope
     *            Contains items monitored by the tabs.
     * @param uri
     *            The local URI for the new location.
     */
    function activateTabBasedOnUri($scope) {

        // Extract first segment of URI .
        var firstUriSegment = $location.path().split('/').slice(1)[0];

        // Set 'activeTab' to be the new URI segment, but only if it actually changed.
        if (firstUriSegment !== $scope.activeTab) {

//            $log.debug("ControllerNavBar: Change tab from '" + $scope.activeTab
//                + "' to '" + firstUriSegment + "'. (URI='"
//                + $location.path() + "')");

            $scope.activeTab = firstUriSegment;
        }
    }
});