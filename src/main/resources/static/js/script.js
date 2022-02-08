const togglePassword = document.querySelector("#togglePassword");
const password = document.querySelector("#password_field");

if (togglePassword) {
	togglePassword.addEventListener("click", function() {

		// toggle the type attribute
		const type = password.getAttribute("type") === "password" ? "text" : "password";
		password.setAttribute("type", type);
		// toggle the eye icon
		this.classList.toggle('fa-eye');
		this.classList.toggle('fa-eye-slash');
	});
}

const toggleSideBar = () => {
	const sidebar = $('.sidebar');
	if (sidebar.is(":visible")) {
		sidebar.css("display", "none");
		$(".content").css("margin-left", "unset");
	} else {
		sidebar.css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

const deleteContact = (cId) => {
	swal({
		title: "Are you sure?",
		text: "Once deleted, you will not be able to recover this Contact!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete-contact/" + cId;
			} else {
				swal("Your Contact is safe!");
			}
		});
}


const updateContact = (cId) => {
	window.location = "/user/update-contact/" + cId;
}

const deleteImg = (cId) => {

	swal({
		title: "Are you sure?",
		text: "Once deleted, you will not be able to recover this Image!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/contact/delete-image/" + cId;
			}
		});

}