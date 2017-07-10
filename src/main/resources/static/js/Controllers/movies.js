

var app = angular.module('movies',[]);

app.controller('movies',['$scope', 'factory', function ($scope, $factory) {

    $scope.moviesShow = true;
    $scope.pageNumber = 0;
    $scope.factsShow = false;

    $scope.searchTitle = "";
    var searchingTime = false;

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

        if(searchingTime){
            $scope.searchMovie();
        }else{
            $scope.loadPage();
        }

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

    $scope.searchMovie = function () {

        if($scope.searchTitle.trim() === ""){
            $scope.pageNumber = 0;
            searchingTime = false;
            $scope.loadPage();
            return;
        }

        if(!searchingTime){
            $scope.pageNumber = 0;
            searchingTime = true;
        }

        $factory.searchMovie($scope.pageNumber,$scope.searchTitle).then(
            function success(response) {
                $scope.movies = response.data;
            }
        );
    }


}]);