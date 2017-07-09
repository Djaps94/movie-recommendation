
var app = angular.module('factory', []).factory('factory',function ($http) {

    var factory = {};

    factory.getMoviesOffset = function (startCount) {
        return $http({
            method: 'GET',
            url: "/movies/offset/"+startCount
        });
    }

    factory.getSimilarMovies = function (movieId){
        return $http({
            method: 'GET',
            url: "/movies/similar/"+movieId
        });
    }

    return factory;
});

