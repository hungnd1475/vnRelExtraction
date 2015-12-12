//js code for annotation

////////////////utils function///////////////
function getNumWordInStr(str) {
	if ($.trim(str).length == 0) {
		return 0;
	}
	var words = $.trim(str).split(/\s+/);
	return words.length;
}
// //////////////////// END ///////////////////

// ////////////////// Contect Menu ///////////////
$(function() {
	var context = $('#emr-doc').nuContextMenu({

		// Omit items if you are attaching to a single container
		// items: '.item',

		callback : function(key, element) {
			// alert('Clicked ' + key + ' on ' + $(element).text());
			element = $(element);
			if (element.prop("tagName").toLowerCase() == "span") {
				insertconcept(element, key);
				if (key == "clear") {
					element.contents().unwrap();
				} else {
					element.removeClass().addClass("selected").addClass(key);
				}
			}
			$('.nu-context-menu').removeClass("active");
		},

		// Define menu items here
		// key: {...}
		menu : {

			'problem' : {
				title : 'Problem',
			},

			'treatment' : {
				title : 'Treatment',
				icon: 'mark',
			},

			'test' : {
				title : 'Test',
			},

			// If the value is 'separator' then an
			// <hr> node is added
			'void' : 'separator',

			'clear' : {
				title : 'Clear'
			},
		}
	});

	// Disable context menu
	// context.nuContextMenu('disable');
});
// ///////////////////////// END : COntext MEnU/////////////////////////////

// //////////////////////////Get and mark selected text////////////////////////
function getSelected(element) {
	var start = 0, end = 0, text = "", line = "";
	var sel, range, priorRange;
	if (typeof window.getSelection != "undefined") {
		text = window.getSelection();

		var parent = text.anchorNode.parentElement;
		var line = $(parent).index() + 1;

		range = window.getSelection().getRangeAt(0);

		console.log(range);
		console.log("\n" + window.getSelection().rangeCount);
		console.log("\n" + window.getSelection());

		priorRange = range.cloneRange();
		var x = priorRange.selectNodeContents(element);

		console.log("\n node contents" + x);

		priorRange.setEnd(range.startContainer, range.startOffset);
		// console.log(priorRange);
		// start = priorRange.toString().length;
		start = getNumWordInStr(priorRange) + 1;
		// end = start + range.toString().length - 1;
		end = start + getNumWordInStr(range) - 1;
	} else {
		alert("Plz use Chrome or Firefox");
	}
	return {
		obj : text,
		line : line,
		start : start,
		end : end
	};
}

function highlight(element, selected) {
	var span = document.createElement("span");
	span.className = "selected";
	span.setAttribute("start", selected.start);
	span.setAttribute("end", selected.end);
	span.setAttribute("line", selected.line);
	element.surroundContents(span);
}

$("#emr-doc").on("mouseup", ".sentence", function() {
	var selected = getSelected($(this).get(0));
	if (selected.obj.toString().length > 0) {
		highlight(selected.obj.getRangeAt(0), selected);
	}
})
// //////////////////////////////////// End //////////////////////////////////

// ///////////////////////////////Add Label///////////////////////////////////
var relation_list = [];
$("#emr-doc").on("click", ".sentence .selected", function(e) {
	if (e.ctrlKey) {
		if (relation_list.length == 1) {
			relation_list[relation_list.length] = $(this);
			var type1 = relation_list[0].attr('class').split(/\s+/)[1];
			var type2 = relation_list[1].attr('class').split(/\s+/)[1];

			if (type1 != "problem" && type2 != "problem") {
				relation_list = [];
			} else if (type1 == "treatment" || type2 == "treatment") {
				relation_list[relation_list.length] = 'tr-p';
				$("#tr-p-dialog").data("obj", relation_list).dialog("open");
				relation_list = [];
			} else if (type1 == "test" || type2 == "test") {
				relation_list[relation_list.length] = 'te-p';
				$("#te-p-dialog").data("obj", relation_list).dialog("open");
				relation_list = [];
			} else if (type1 == "problem" || type2 == "problem") {
				relation_list[relation_list.length] = 'p-p';
				$("#p-p-dialog").data("obj", relation_list).dialog("open");
				relation_list = [];
			}
		} else {
			relation_list[relation_list.length] = $(this);
		}
	} else {
		$("#dialog").data("obj", $(this)).dialog("open");
	}
	// console.log($(this));
})

$("#dialog").dialog(
		{
			resizable : false,
			draggable : false,
			autoOpen : false,
			modal : true,
			width : 200,
			buttons : {
				OK : function() {
					var label = $("input[name=label]:checked").val();
					insertconcept($("#dialog").data("obj"), label);
					if (label == "clear") {
						$("#dialog").data("obj").contents().unwrap();
					} else {
						$("#dialog").data("obj").removeClass().addClass(
								"selected").addClass(label);
					}
					$(this).dialog("close");
				},
				Cancel : function() {
					$(this).dialog("close");
				}
			}
		})

// ======================= Relation Dialog ================================
$("#tr-p-dialog").dialog({
	resizable : false,
	draggable : false,
	autoOpen : false,
	modal : true,
	width : 500,
	buttons : {
		OK : function() {
			var label = $("input[name=label]:checked").val();
			insertRelation($("#tr-p-dialog").data("obj"), label);
			$(this).dialog("close");
		},
		Cancel : function() {
			$(this).dialog("close");
		}
	}
});

$("#te-p-dialog").dialog({
	resizable : false,
	draggable : false,
	autoOpen : false,
	modal : true,
	width : 500,
	buttons : {
		OK : function() {
			var label = $("input[name=label]:checked").val();
			insertRelation($("#tr-e-dialog").data("obj"), label);
			$(this).dialog("close");
		},
		Cancel : function() {
			$(this).dialog("close");
		}
	}
});

$("#p-p-dialog").dialog({
	resizable : false,
	draggable : false,
	autoOpen : false,
	modal : true,
	width : 500,
	buttons : {
		OK : function() {
			var label = $("input[name=label]:checked").val();
			insertRelation($("#p-p-dialog").data("obj"), label);
			$(this).dialog("close");
		},
		Cancel : function() {
			$(this).dialog("close");
		}
	}
});
// //////////////////////////////////// End //////////////////////////////////

// ///////////////////////////////Add concept/////////////////////////////////
function insertconcept(element, label) {
	var line = element.attr("line");
	var start = element.attr("start");
	var end = element.attr("end");
	var string = element.text();

	var selected = 'c="' + string + '" ' + line + ":" + start + " " + line
			+ ":" + end;
	var concept = selected + '||t="' + label + '"';

	// Get angular scope of emrList controller
	var scope = angular.element(document.getElementById("main")).scope();
	scope.$apply(function() {
		var found = false;
		for (var i = 0; i < scope.concepts.length; i++) {
			var exist = scope.concepts[i].indexOf(selected);
			if (exist > -1) {
				found = true;
				if (label != "clear") {
					scope.concepts[i] = concept;
				} else {
					scope.concepts.splice(i, 1);
				}
			}
		}
		if (found == false && label != "clear") {
			scope.concepts.push(concept);
		}
	})
}

// ///////////////////////////Add Relation /////////////////////////
function insertRelation(elements, label) {
	// c="a amiodarone gtt" 75:11 75:13||r="TrAP"||c="burst of atrial
	// fibrillation" 75:3 75:6

	var line1 = elements[0].attr("line");
	var start1 = elements[0].attr("start");
	var end1 = elements[0].attr("end");
	var string1 = elements[0].text();
	var type1 = elements[0].attr('class').split(/\s+/)[1];

	var line2 = elements[1].attr("line");
	var start2 = elements[1].attr("start");
	var end2 = elements[1].attr("end");
	var string2 = elements[1].text();
	var type2 = elements[1].attr('class').split(/\s+/)[1];

	var selected1 = 'c="' + string1 + '" ' + line1 + ":" + start1 + " " + line1
			+ ":" + end1;
	var selected2 = 'c="' + string2 + '" ' + line2 + ":" + start2 + " " + line2
			+ ":" + end2;
	var relation = '';

	if (type1 == "problem" && type2 == "problem") {
		relation = selected1 + '||r="' + label + '"||' + selected2;
	} else if (type1 == "problem") {
		relation = selected2 + '||r="' + label + '"||' + selected1;
	} else if (type2 == "problem") {
		relation = selected1 + '||r="' + label + '"||' + selected2;
	} else {
		return;
	}
	console.log("\n" + relation + "\n");

	// Get angular scope of emrList controller
	var scope = angular.element(document.getElementById("wrap")).scope();
	scope.$apply(function() {
		var found = false;
		for (var i = 0; i < scope.relations.length; i++) {
			var exist1 = scope.relations[i].indexOf(selected1);
			var exist2 = scope.relations[i].indexOf(selected2);
			// console.log(exist);
			if (exist1 > -1 && exist2 > -1) {
				found = true;
				if (label != "clear") {
					scope.relations[i] = relation;
				} else {
					scope.relations.splice(i, 1);
				}
			}
		}
		if (found == false && label != "clear") {
			scope.relations.push(relation);
		}
	})
}
// //////////////////////////////////// End //////////////////////////////////
