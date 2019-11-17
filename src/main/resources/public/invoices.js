angular.module('invoices', [])
.controller('Invoices', function($scope, $http, $window) {
    $http.get(getBaseApiAddress()).
        then(function(response) {
            $scope.invoices = response.data;
        });

    $scope.pdf = function(id) {
        $window.open(getBaseApiAddress() + 'pdf/' + id);
    }

    $scope.delete = function(id) {
        $http.delete(getBaseApiAddress() + id).
            then(function() {
                $http.get(getBaseApiAddress()).
                    then(function(response) {
                        $scope.invoices = response.data;
                    });
            });
    }
});

function getMainSite() {
    $http.get(getBaseApiAddress()).
    then(function(response) {
        $scope.invoices = response.data;
    });
}

function getBaseApiAddress() {
    return 'http://localhost:8080/invoices/';
}
