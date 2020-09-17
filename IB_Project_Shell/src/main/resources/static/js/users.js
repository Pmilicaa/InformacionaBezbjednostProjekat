
	function fillTable(){
		tableHeader();
		$.ajax({
			url:'http://localhost:8443/active',
			type: 'GET',
			success:function(response){
				if(response.length == 0){
					var table =  $('#myTable');
					table.empty();
					return;
				}
				for(var i=0; i<response.length; i++) {
					var table =  $('#myTable');
					user = response[i];
					console.log(user.email);
					table.append('<tr>'+
									'<td>'+user.email+'</td>'+
									'<td><button onclick="downloadCer(\''+user.email+'\')" class="btn btn-info">Download</button></td>'+
								'</tr>');
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {  
				console.log(textStatus+" "+jqXHR.status)
			}
		});
	}
	
	
	
	function fillInactiveTable(){
		tableInactiveHeader();
		$.ajax({
			url:'http://localhost:8443/inactive',
			type: 'GET',
			crossDomain: true,
			success:function(response){
				if(response.length == 0){
					var table =  $('#myInactiveTable');
					table.empty();
					return;
				}
				for(var i=0; i<response.length; i++) {
					var table =  $('#myInactiveTable');
					user = response[i];
					table.append('<tr>'+
									'<td>'+user.email+'</td>'+
									'<td><button onclick="activateUser('+user.id+')" class="btn btn-info">Activate</button></td>'+
								'</tr>');
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {  
				alert(textStatus+" "+jqXHR.status)
			}
		});
	}
	
	function tableHeader(){
		var table =  $('#myTable');
		table.empty();
		table.append('<tr>'+
					'<th>Aktivni korisnici</th>'+
					'<th><button onclick="fillTable()" class="btn btn-info">Refresh</button></th>'+
					'</tr>'+
					'<tr>'+
						'<th>Email</th>'+
						'<th>Download</th>'+
					'</tr>');
	}
	
	
	function tableInactiveHeader(){
		var table =  $('#myInactiveTable');
		table.empty();
		table.append('<tr>'+
					'<th>Neaktivni korisnici</th>'+
					'<th><button onclick="fillInactiveTable()" class="btn btn-info">Refresh</button></th>'+
					'</tr>'+
	
					'<tr>'+
						'<th>Email</th>'+
						'<th>Activate</th>'+
					'</tr>');
	}
	
	function activateUser(id){
		$.ajax({
			type: 'POST',
	        url: 'http://localhost:8443/activate/'+id,
	        
			success:function(response){
				alert("User activated.");
				fillTable();
				fillInactiveTable();
				
			},
			error: function (jqXHR, textStatus, errorThrown) {  
				alert(textStatus+" "+jqXHR.status)
			}
		});
	}
	
	
	$("#logoutButton").on('click', function(){
		
		window.location.replace("http://localhost:8443/login.html");

	});
	
	function downloadCer(userName){
		
		var xhr = new XMLHttpRequest();
		xhr.open('GET', "http://localhost:8443/api/demo/download/"+userName, true);
		xhr.responseType = 'blob';
	
		xhr.onload = function(e) {
			console.log("Is this called?")
			console.log(userName);
			console.log(this.response);
			if (this.status == 200) {
				console.log("Are you there")
				var blob = this.response;
				console.log(blob);
				var a = document.createElement('a');
				var url = window.URL.createObjectURL(blob);
				a.href = url;
				a.download = xhr.getResponseHeader('filename');
				a.click();
				window.URL.revokeObjectURL(url);
			}
		};
	
		xhr.send();
	}
	fillInactiveTable();
	fillTable();
	
	
	
