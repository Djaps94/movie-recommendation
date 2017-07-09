

var app = angular.module('movies',[]);

app.controller('movies',['$scope', 'factory', function ($scope, $factory) {

    $scope.moviesShow = true;
    $scope.pageNumber = 0;
    $scope.factsShow = false;

    $scope.movies = [];
    $scope.similar = [];

    $scope.loadPage = function (){
        $factory.getMoviesOffset($scope.pageNumber).then(
            function success(response) {
                $scope.movies = response.data;
            }
        );
    }

    $scope.loadPage();

    $scope.nextPage = function (newPageNumber) {
        if(newPageNumber > 455 || newPageNumber < 0){
            return;
        }
        $scope.pageNumber = newPageNumber;
        $scope.loadPage();
    }


    $scope.showMovie = function (movieToShow) {
        $scope.moviesShow = false;
        $scope.movieToShow = movieToShow;
        $factory.getSimilarMovies(movieToShow.id).then(
            function success(response) {
                $scope.similar = response.data;
            }
        );
    }

    $scope.showMovies = function () {
        $scope.moviesShow = true;
    }

    $scope.facts = function() {
        if($scope.factsShow)
            $scope.factsShow = false;
        else
            $scope.factsShow = true;
    }




}]);