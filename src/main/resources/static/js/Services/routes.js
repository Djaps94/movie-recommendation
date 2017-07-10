

var app = angular.module('routes',['ngRoute']);


app.config(['$routeProvider',function ($routeProvider) {


    $routeProvider
        .when('/',{
            templateUrl: 'login.html',
            controller: 'login'
        })
        .when('/login',{
            templateUrl: 'login.html',
            controller: 'login'
        })
        .when('/movies',{
            templateUrl: 'movies.html',
            controller: 'movies'
        });

}]);


