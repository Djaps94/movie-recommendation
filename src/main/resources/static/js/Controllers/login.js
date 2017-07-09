/**
 * Created by Sasa on 07-Jul-17.
 */

var app = angular.module('login',[]);


app.controller('login', ['$scope', 'factory', '$location', function ($scope, $factory, $location) {

    $scope.registerShow = false;

    $scope.showRegister = function (show) {
        $scope.registerShow = show;
    }


    $scope.login = function () {
        $location.path('movies')
    }


    $scope.register = function () {
        alert("Register");
    }

}]);
