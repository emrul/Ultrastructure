var usBrowserControllers=angular.module("usBrowserControllers",[]);usBrowserControllers.controller("FacetListCtrl",["$scope","$http","$routeParams",function(r,t,e){t.get("/json-ld/facet/"+e.ruleform).success(function(t){r.facets=t})}]),usBrowserControllers.controller("FacetRuleformsListCtrl",["$scope","$http",function(r,t){t.get("/json-ld/facet").success(function(t){r.facetRuleforms=t})}]),usBrowserControllers.controller("FacetDetailCtrl",["$scope","$http","$routeParams",function(r,t,e){t.get("/json-ld/facet/$routeParams.ruleform/$routeParams.instance").success(function(t){r.facet=t})}]);