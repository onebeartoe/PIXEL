
function changeControls(mode)
{
    switch(mode)
    {
        case "animations":
        {
            break;
        }
        case "still":
        {
            hideElement("time-lapse-controls");
            
            break;
        }
        case "text":
        {
            
            break;
        }
        default:
        {
            // "time-lapse"
            showElement("time-lapse-controls");
        }
    }
}

function hideElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'none';
}

function loadAnimations()
{
    
}

function loadImageResources()
{
    loadStillImages();
    loadAnimations();
}

function loadStillImages()
{
    logEvent("loading still images...");
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var list = xmlhttp.responseText;
            
            var names = list.split("-+-");
            
            var e = document.getElementById("still-images");

            e.innerHTML = "<div>";
            
            for(var n in names)
            {
                var name = names[n].trim();
                
                if(name != "")
                {
                    e.innerHTML += "<div>" + "\n";
                    e.innerHTML += "\t" + "<img src=\"/files/images/" + name + "\">" + "\n";
                    e.innerHTML += name + "\n";
                    e.innerHTML += "</div>" + "\n";
                }
            }
            
            e.innerHTML += "</div>";
            
            logEvent("done loading still images");
        }
    }
    var url = "/still/list";    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");    
}

function logServerResponse(xmlhttp)
{
    if (xmlhttp.readyState==4 && xmlhttp.status==200)      
    {
        var s = xmlhttp.responseText;
        logEvent(s);
    }
}

function logEvent(message)
{
    var e = document.getElementById("logs");
    
    var logs = message + "<br/>" + e.innerHTML;
    
    e.innerHTML = logs;
}

function modeChanged(mode)
{
    changeControls(mode);
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        logServerResponse(xmlhttp);
    }
    var url = "/" + mode;    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");
}

function showElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'block';
}
