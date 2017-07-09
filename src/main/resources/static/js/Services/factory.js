/**
 * Created by Sasa on 08-Jul-17.
 */

var app = angular.module('factory', []).factory('factory',function ($http) {

    var factory = {};

    factory.getMoviesOffset = function (startCount) {
        return $http({
            method: 'GET',
            url: "/movies/offset/"+startCount
        });
    }

    return factory;
});

