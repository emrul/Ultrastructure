var phantasm=angular.module("phantasm",["restangular"]);phantasm.config(["RestangularProvider",function(n){n.setBaseUrl("/json-ld")}]),phantasm.factory("Facet",["Restangular",function(n){var t=n.service("facet");return t}]),phantasm.factory("WorkspaceMediated",["Restangular",function(n){var t=n.service("workspace-mediated");return t}]),phantasm.service("PhantasmRelative",function(){this.translateIdToFacetInstance=function(n){n["@id"]=this.instance(n["@id"])},this.facet=function(n){var t=n.split("/"),e="",a=t.slice(t.length-3,t.length);for(var r in a)e=r>0?e+"/"+a[r]:a[r];return e},this.instance=function(n){return n.substr(n.lastIndexOf("/")+1)},this.facetInstance=function(n){var t=n.split("/"),e="",a=t.slice(t.length-3,t.length);for(var r in a)e=r>0?e+"/"+a[r]:a[r];return e},this.fullyQualifiedInstance=function(n){var t=n.split("/"),e="",a=t.slice(t.length-4,t.length);for(var r in a)e=r>0?e+"/"+a[r]:a[r];return e},this.facetInstances=function(n){var t=n.split("/"),e="",a=t.slice(t.length-4,t.length);for(var r in a)e=r>0?e+"/"+a[r]:a[r];return e}}),phantasm.factory("Phantasm",["Facet",function(n){var t={};return t.facetInstance=function(n,t,e,a){var r=this.facet(n,t,e);return r.one(a)},t.facet=function(t,e,a){return n.one(t).one(e).one(a)},t.facetInstances=function(n,t,e){var a=this.facet(n,t,e);return a.one("instances")},t}]),phantasm.factory("WorkspacePhantasm",["WorkspaceMediated",function(n){var t={};return t.facet=function(t,e,a,r){return n.one(t).one("facet").one(e).one(a).one(r)},t.facetInstance=function(n,t,e,a,r){var s=this.facet(n,t,e,a);return s.one(r)},t.facetInstances=function(n,t,e,a){var r=this.facet(n,t,e,a);return r.one("instances")},t}]);