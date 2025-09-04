async function test(){
    const input = document.getElementById("text").value;
    
    const response = await fetch("test?input="+input);
    
    if(response.ok){
        
    }else{
        
    }
}