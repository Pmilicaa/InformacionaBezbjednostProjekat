$(document).ready(function(){
	
	$('#registrationSubmit').on('click', function(){
		
			var email =  $('#korisnickoImeInput').val().trim();
			var password = $('#lozinkaInput').val().trim();
			console.log(email+" "+password);
			if(email=="" || password==""){
				alert("All fields must be filled.")
				return;
			}
			
			var data = {
					'email':email,
					'password':password
			}
			console.log(data);
			
			$.ajax("users/register", {
			   type: "POST",
			   data: data,
			   statusCode: {
			      200: function (response) {
			         $.ajax({
						type: 'POST',
						 url: 'http://localhost:8443/create/' + email +"/" +password,
							cache: false,
						  success :function(answer) {
							  alert("Imate certificate i jks");
						  }
					});
			      },
			      400: function (response) {
			         alert('Korisnik vec postoji');
			      },
			      404: function (response) {
			         alert('');
			      }
			   }, success: function () {
					alert("Bravo, registrovali ste se! Morate biti prihvaceni od strane administratora da biste se ulogovali.")
			   },
			});
			
		/*	$.post("users/register",data, function(response){
					console.log(response);
					alert("Bravo, registrovali ste se! Morate biti prihvaceni od strane administratora da biste se ulogovali.")
					
					$.ajax({
						type: 'POST',
						 url: 'http://localhost:8443/create/' + email +"/" +password,
							cache: false,
						  success :function(answer) {
							  alert("You have certificate and jks");
						  }
						 
						 
						
					});
			});*/
			
			
		

	});
	
});