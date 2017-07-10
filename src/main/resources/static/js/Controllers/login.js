var app = angular.module('login',[]);


app.controller('login', ['$scope', 'factory', '$location', '$timeout', function ($scope, $factory, $location, $timeout) {

    $scope.registerShow = false;

    $scope.registerUsername = "";
    $scope.registerPassword = "";
    $scope.confirmPassword = "";

    $scope.showRegister = function (show) {
        $scope.registerShow = show;
    }


    $scope.login = function () {
        $location.path('movies')
    }


    $scope.register = function (username, password, confirm) {
        if(username === "" || password === "" || confirm === "")
            return;

        if(password !== confirm)
            return;

        var user = {
            username : username,
            password : password
        };

        $factory.register(user).then(
           function success(response){
               if(response == null){
                   $scope.registerUsername = "";
                   $scope.registerPassword = "";
                   $scope.confirmPassword = "";
                   return;
               }
               localStorage.setItem("user", response.data);
               $timeout(function(){
                   $scope.registerShow = false;
               }, 2000);
           }
        );
    }

}]);
