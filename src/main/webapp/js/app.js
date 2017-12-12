angular.module('cmsApp', ['ngRoute', 'ngCookies', 'cmsApp.services'])
    .config(
        ['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {

            $routeProvider.when('/create', {
                templateUrl: 'partials/create.html',
                controller: CreateController
            });

            $routeProvider.when('/edit/:id', {
                templateUrl: 'partials/edit.html',
                controller: EditController
            });

            $routeProvider.when('/login', {
                templateUrl: 'partials/login.html',
                controller: LoginController
            });

            $routeProvider.otherwise({
                templateUrl: 'partials/index.html',
                controller: IndexController
            });

            $locationProvider.hashPrefix('!');

            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
                    return {
                        'responseError': function (rejection) {
                            var status = rejection.status;
                            var config = rejection.config;
                            var method = config.method;
                            var url = config.url;

                            if (status == 401) {
                                $location.path("/login");
                            } else {
                                $rootScope.error = method + " on " + url + " failed with status " + status;
                            }

                            return $q.reject(rejection);
                        }
                    };
                }
            );

            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
                    return {
                        'request': function (config) {
                            var isRestCall = config.url.indexOf('api') == 0;
                            if (isRestCall && angular.isDefined($rootScope.accessToken)) {
                                var accessToken = $rootScope.accessToken;
                                if (cmsAppConfig.useAccessTokenHeader) {
                                    config.headers['X-Access-Token'] = accessToken;
                                } else {
                                    config.url = config.url + "?token=" + accessToken;
                                }
                            }
                            return config || $q.when(config);
                        }
                    };
                }
            );

        }]
    ).run(function ($rootScope, $location, $cookieStore, UserService) {

    $rootScope.$on('$viewContentLoaded', function () {
        delete $rootScope.error;
    });

    $rootScope.hasRole = function (role) {

        if ($rootScope.user === undefined) {
            return false;
        }

        if ($rootScope.user.roles[role] === undefined) {
            return false;
        }

        return $rootScope.user.roles[role];
    };

    $rootScope.logout = function () {
        delete $rootScope.user;
        delete $rootScope.accessToken;
        $cookieStore.remove('accessToken');
        $location.path("/login");
    };

    /* Try getting valid user from cookie or go to login page */
    var originalPath = $location.path();
    $location.path("/login");
    var accessToken = $cookieStore.get('accessToken');
    if (accessToken !== undefined) {
        $rootScope.accessToken = accessToken;
        UserService.get(function (user) {
            $rootScope.user = user;
            $location.path(originalPath);
        });
    }

    $rootScope.initialized = true;
});

function IndexController($scope, DocumentService) {

    $scope.documents = DocumentService.query();

    $scope.deleteDocument = function (document) {
        document.$remove(function () {
            $scope.documents = DocumentService.query();
        });
    };
}

function EditController($scope, $routeParams, $location, DocumentService) {

    $scope.document = DocumentService.get({id: $routeParams.id});

    $scope.save = function () {
        $scope.document.$save(function () {
            $location.path('/');
        });
    };
}

function CreateController($scope, $location, DocumentService) {

    $scope.document = new DocumentService();

    $scope.save = function () {
        $scope.document.$save(function () {
            $location.path('/');
        });
    };
}

function LoginController($scope, $rootScope, $location, $cookieStore, UserService) {

    $scope.rememberMe = false;

    $scope.login = function () {
        UserService.authenticate($.param({
            username: $scope.username,
            password: $scope.password
        }), function (authenticationResult) {
            var accessToken = authenticationResult.token;
            $rootScope.accessToken = accessToken;
            if ($scope.rememberMe) {
                $cookieStore.put('accessToken', accessToken);
            }
            UserService.get(function (user) {
                $rootScope.user = user;
                $location.path("/");
            });
        });
    };
}

var services = angular.module('cmsApp.services', ['ngResource']);

services.factory('UserService', function ($resource) {

    return $resource('api/user/:action', {},
        {
            authenticate: {
                method: 'POST',
                params: {'action': 'authenticate'},
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }
        }
    );
});

services.factory('DocumentService', function ($resource) {

    return $resource('api/documents/:id', {id: '@id'});
});
