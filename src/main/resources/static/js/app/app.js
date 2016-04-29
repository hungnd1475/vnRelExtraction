var app = angular.module('vnEMRRE', []);
app.filter('unsafe', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
});

app.controller('vnTraining', function($scope, $sce, $http) {
	$('.id-input-file-3').ace_file_input({
		style:'well',
		btn_choose:'Drop files here or click to choose',
		btn_change:null,
		no_icon:'ace-icon glyphicon-cloud',
		droppable:true,
		thumbnail:'small'//large | fit
		,
		preview_error : function(filename, error_code) {
			
		}
	
	}).on('change', function(){
	});
	
	var urlBase = "training/";
	
	$scope.run = function() {
		$('#refresh').show();
		var req = $http({
            method : "post",
            url : urlBase + "start",
            data : {
                action: "training"
            },
        });
		
		req.success(function(res) {
			$('#refresh').hide();
			if (res.err >= 0) {
	            new PNotify({
	                title : "Training Process",
	                text : "Success",
	                type : "success",
	                delay : 1000
	            });
			} else {
				new PNotify({
	                title : "Training Process",
	                text : "Fail",
	                type : "Fail",
	                delay : 1000
	            });
			}
        });
	}
});

app.controller('vnTrainingModel', function($scope, $sce, $http) {
});

app.controller('vnExtractor', function($scope, $sce, $http) {
    $scope.record = "";
    $scope.sentences = [];
    $scope.rawHtmlSentences = [];
    $scope.concepts = [];
    $scope.relations = [ "empty", "there is nothing here!" ];
    $scope.editMode = true;
    $scope.mode = "automatic";
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

    $scope.getContent = function() {
        $http.get('http://localhost/getFileContent.php?file=10.txt').success(
                function(data) {
                    $scope.sentences = data.content;
                    $scope.concepts = data.mention;
                })
    }

    $scope.run = function() {
        if ($scope.mode == "automatic") {
            if ($scope.record != "") {
                var req = $http({
                    method : "post",
                    url : urlBase + "automatic",
                    data : {
                        record : $scope.record
                    },
                    headers : {
                        'Content-Type' : 'application/json'
                    }
                });

                req.success(function(res) {
                    $scope.concepts = res.concepts;
                    $scope.sentences = res.sentences;
                    $scope.relations = res.relations;
                    $scope.editMode = false;
                    new PNotify({
                        title : "Extracted!",
                        text : "Success",
                        type : "success",
                        delay : 1000
                    });
                });
            }
        } else if ($scope.mode = "manual") {
            if ($scope.record != "") {
                var req = $http({
                    method : "post",
                    url : urlBase + "manual",
                    data : {
                        sentences : $scope.sentences,
                        concepts : $scope.concepts
                    },
                    headers : {
                        'Content-Type' : 'application/json'
                    }
                });

                req.success(function(res) {
                    $scope.relations = res.relations;
                    $scope.editMode = false;
                    new PNotify({
                        title : "Extracted!",
                        text : "Success",
                        type : "success",
                        delay : 1000
                    });
                });
            }
        }
        if ($scope.record != "" && $scope.concepts != "") {

        }
        console.log($scope.mode);
    }

    $scope.clearConcepts = function() {
        $scope.concepts = [];
        $scope.sentences = [];
        $scope.sentences = $scope.rawHtmlSentences;
        location.reload();
    }

});