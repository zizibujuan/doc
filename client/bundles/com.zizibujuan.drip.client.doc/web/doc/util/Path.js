var SEPARATOR = "/";

function Path(pathname){
	// 去掉开始和结尾的"/"
	var p = pathname.replace(/^\/+|\/+$/g, '');
	// 去掉重复的"/"
	if (p.indexOf("//", 1) != -1){
		p = this.collapseSlashes(p);
	}
	this.segments = p.split(SEPARATOR);
}

Path.prototype.collapseSlashes = function(pathname){
	var result = [];
	var len = pathname.length;
	var hasPrevious = false;
	var count = 0;
	for(var i = 0; i < len; i++){
		var c = pathname.charAt(i);
		if(c == SEPARATOR){
			if(hasPrevious){
				
			}else{
				hasPrevious = true;
				result[count] = c;
				count++;
			}
		}else{
			hasPrevious = false;
			result[count] = c;
			count++;
		}
	}
	return result.join("");
},

Path.prototype.segmentCount = function(){
	return this.segments.length;
},
Path.prototype.segment = function(index){
	// summary:
	//		
	// index: Integer
	//		从0开始
	
	return this.segments[index];
}