const toggleSideBar = () => {
	const sidebar = $('.sidebar');
	if (sidebar.is(":visible")) {
		sidebar.css("display", "none");
		$(".content").css("margin-left", "unset");
		//$(".menuBar").css("display", "block");
	} else {
		sidebar.css("display", "block");
		$(".content").css("margin-left", "20%");
	//	$(".menuBar").css("display", "none");
	}
};

