
function changeControls(mode)
{
    switch(mode)
    {
        case "animations":
        {
            hideElement("stillPanel");
            
            showElement("animationsPanel");
            
            break;
        }
        case "still":
        {
            hideElement("animationsPanel");
            
            showElement("stillPanel");
            
            break;
        }
        case "text":
        {
            hideElement("animationsPanel");
            hideElement("stillPanel");
            
            break;
        }
        default:
        {
            // "time-lapse"
            showElement("stillPanel");
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
            
            

            var html = "<table>";
            
            var c = 0;
            var columns = 4;
            
            for(var n in names)
            {
                var name = names[n].trim();
                
                if(c === columns)
                {
                    html += "<tr>";
                }
                
                if(name != "")
                {
                    html += "<td>";
                    html += "<img src=\"/files/" + imagePath + name + "\" " + 
                                   "width=\"50\" height=\"50\" alt=\"" + name +  "\"" +  
                                   ">";
                    html += "<br/>";
                    html += "<button onclick=\"displayImage('" + imagePath + "', '" + name + "')\">Display</button>";
                    html += "<p>" + name + "</p>";
                    html += "</td>";
                }
                
                if(c < columns)
                {
                    c++;
                }
                else
                {
                    html += "</tr>";
                    c = 0;
                }
            }
            
            html += "<div class=\"spacer\">&nbsp;</div>";
            
            html += "</table>";
            
            var e = document.getElementById(elementName);
            e.innerHTML = html;
            
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

function modeChanged(mode, imageName)
{
    if(imageName === null)
    {
        imageName = "";
    }
    
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
