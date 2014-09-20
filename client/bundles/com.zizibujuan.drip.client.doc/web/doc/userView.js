
function showUserZone(){
	// 如果用户已经登录，则显示用户信息
	$.getJSON("/users/").done(function(data){
		// 当前用户已登录
		$("#linkUser").attr("href", "/users/" + data.id);
		$("#imgUser").attr("alt", data.nickName);
		var smallImageUrl = data.smallImageUrl;
		if(smallImageUrl != null && smallImageUrl != ""){
			$("#imgUser").attr("src", smallImageUrl);
		}else{
			$("#imgUser").attr("src", "/useradmin/resources/images/profile_50_50.gif");
		}
		$("#spanUser").html(data.nickName || data.loginName);
		
	}).fail(function(error){
		// 当前用户没有登录
		console.info("用户没有登录");
	});
}

