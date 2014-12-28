
function changeControls(mode)
{
    switch(mode)
    {
        case "animations":
        {
            hideElement("still");
            
            showElement("animations");
            
            break;
        }
        case "still":
        {
            hideElement("animations");
            
            showElement("still");
            
            break;
        }
        case "text":
        {
            hideElement("animations");
            hideElement("still");
            
            break;
        }
        default:
        {
            // "time-lapse"
            showElement("time-lapse-controls");
        }
    }
}

function displayImage(imagePath, name)
{
    var mode;
    
    switch(imagePath)
    {
        case "animations/":
        {
            mode = "animation/";
            break;
        }
        default:
        {
            // still images
            mode = "still/";
        }
    }
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        logServerResponse(xmlhttp);
    }
    var url = "/" + mode + name;
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");    
}

function hideElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'none';
}

function loadAnimations()
{
    var url = "/animation/list";
    var elementName = "animations";
    var imagePath = "animations/";
    
    loadImageList(url, elementName, imagePath);
}

function loadImageList(url, elementName, imagePath)
{
    logEvent("loading " + elementName + "...");
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var list = xmlhttp.responseText;
            
            var names = list.split("-+-");
            
            var e = document.getElementById(elementName);

            e.innerHTML = "<div>";
            
            for(var n in names)
            {
                var name = names[n].trim();
                
                if(name != "")
                {
                    e.innerHTML += "<div>" + "\n";
                    e.innerHTML += "\t" + "<img src=\"/files/" + imagePath + name + "\">" + "\n";
                    e.innerHTML += "\t" + "<button onclick=\"displayImage('" + imagePath + "', '" + name + "')\">Display</button>" + "\n";
                    e.innerHTML += name + "\n";
                    e.innerHTML += "</div>" + "\n";
                }
            }
            
            e.innerHTML += "</div>";
            
            logEvent("done loading " + elementName);
        }
    }
    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");    
}

function loadImageResources()
{
    loadStillImages();
    loadAnimations();
}

function loadStillImages()
{
    var url = "/still/list";
    var elementName = "still";
    var imagePath = "images/";
    
    loadImageList(url, elementName, imagePath);
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

function modeChanged(mode, imageName = "")
{
    changeControls(mode);
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        logServerResponse(xmlhttp);
    }
    var url = "/" + mode;
    
    if(imageName != "")
    {
        url += "/" + imageName;
    }
    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");
}

function showElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'block';
}
