$(document).ready(function(){
	
	$('#loginSubmit').on('click', function(){
		
	
			var email =  $('#korisnickoImeInput').val().trim();
			var password = $('#lozinkaInput').val().trim();
			if(email=="" || password==""){
				alert("All fields must be filled.")
				return;
			}
			var data = {
					'email':email,
					'password':password
			}
			console.log(data);
			
			$.ajax({
				type: 'POST',
		        url: 'http://localhost:8443/login',
		       
				success:function(response){
					
					$('#loginModal').modal('toggle');
					
					window.location.href = "users.html";
					
				},
				error: function (jqXHR, textStatus, errorThrown) {  
					if(jqXHR.status=="401"){
						alert("Wrong email or password.");
					}
				}
			});
		
	});
});