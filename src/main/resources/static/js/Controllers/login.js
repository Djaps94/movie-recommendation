var app = angular.module('login',[]);


app.controller('login', ['$scope', 'factory', '$location', '$timeout', function ($scope, $factory, $location, $timeout) {

    if(localStorage.getItem("user") != null)
        $location.path('movies');

    $scope.registerShow = false;

    $scope.registerUsername = "";
    $scope.registerPassword = "";
    $scope.confirmPassword = "";

    $scope.username = "";
    $scope.password = "";

    $scope.showRegister = function (show) {
        $scope.registerShow = show;
    }


    $scope.login = function (username, password) {
        if(username === "" || password === "")
            return;

        $factory.login(username, password).then(
            function success(response){
                if(response.data === "" || response.data == null)
                    return;

                localStorage.setItem("user", response.data);
                $timeout(function(){
                    $location.path('movies')
                }, 1000);
                $scope.username = "";
                $scope.password = "";
            }
        );
    }


    $scope.register = function (username, password, confirm) {
        if(username === "" || password === "" || confirm === "")
            return;

        if(password !== confirm)
            return;

        $factory.register(username, password).then(
           function success(response){
               if(response.data == null){
                   $scope.registerUsername = "";
                   $scope.registerPassword = "";
                   $scope.confirmPassword = "";
                   return;
               }

               $timeout(function(){
                   $scope.registerShow = false;
                   $scope.registerUsername = "";
                   $scope.registerPassword = "";
                   $scope.confirmPassword = "";
               }, 2000);
           }
        );
    }

}]);
