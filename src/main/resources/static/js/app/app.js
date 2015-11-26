var app = angular.module('vnEMRRE', []);
app.filter('unsafe', function($sce) {
	return function(val) {
		return $sce.trustAsHtml(val);
	};
});

app.controller('vnExtractor', function($scope, $http) {
	$scope.record = "";
	$scope.sentences = [];
	$scope.rawHtmlSentences = [];
	$scope.concepts = [];
	$scope.relations = [ "empty", "there is nothing here!" ];
	$scope.editMode = true;
	$scope.mode = "" ;
	var urlBase = "vn-extractor/";

	$scope.postRecord = function() {
		if ($scope.record != "") {
			var req = $http({
				method : "post",
				url : urlBase + "preprocess",
				data : {
					record : $scope.record
				},
				headers : {
					'Content-Type' : 'application/json'
				}
			});

			req.success(function(res) {
				// $scope.record = res.record;
				$scope.sentences = res.sentences;
				$scope.rawHtmlSentences = res.sentences;
				$scope.editMode = !$scope.editMode;
				console.log($scope.sentences);
				new PNotify({
					title : "Pre Process Text",
					text : "Success",
					type : "success",
					delay : 1000
				});
			});

		}
	}

	$scope.editRecord = function() {
		$scope.editMode = !$scope.editMode;
	}

	$scope.run = function() {
		if($scope.mode == "automatic"){
			
		}else if($scope.mode = "manual"){
			
		}
		if ($scope.record != "" && $scope.concepts != "") {
			
		}
		console.log($scope.mode);
	}

	$scope.clearConcepts = function() {
		$scope.concepts = [];
		console.log($scope.sentences);
		$scope.sentences = [];
		$scope.sentences = $scope.rawHtmlSentences;
		location.reload();
	}

});