/**
 * Created by Sasa on 08-Jul-17.
 */

var app = angular.module('movies',[]);

app.controller('movies',['$scope', 'factory', function ($scope, $factory) {

    $scope.pageNumber = 0;

    $scope.movies = [];

    $scope.loadPage = function (){
        $factory.getMoviesOffset($scope.pageNumber).then(
            function success(response) {
                $scope.movies = response.data;
            }
        );
    }

    $scope.nextPage = function (newPageNumber) {
        if(newPageNumber > 455 || newPageNumber < 0){
            return;
        }
        $scope.pageNumber = newPageNumber;
        $scope.loadPage();
    }

    $scope.loadPage();

}]);