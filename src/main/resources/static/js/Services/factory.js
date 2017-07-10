
var app = angular.module('factory', []).factory('factory',function ($http) {

    var factory = {};


    //MOVIES

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

    factory.searchMovie = function (pageNumber,movieTitle) {
        return $http({
            method: 'GET',
            url: "/movies/search/"+pageNumber+"/"+movieTitle

        });
    }

    // RATING

    factory.getMovieRatings = function(movie){
        return $http({
            method: 'GET',
            url: "/rating/"+movie.id
        });
    }

    factory.rateMovie = function (movieID, userID, rate) {
        return $http({
            method: 'POST',
            url: "/rating/rate/"+movieID+"/"+userID+"/"+rate
        });
    }

    // USERS

    factory.register = function(username, password){
        return $http({
            method : 'POST',
            url: "/users/register/"+username+"/"+password,
        });
    }

    factory.login = function(username, password){
        return $http({
            method : 'POST',
            url : "/users/login/"+username+"/"+password,
        });
    }



    return factory;
});

