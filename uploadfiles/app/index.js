const uploadFile = (file, url, token, webhook, requestid) => {
  console.log("Uploading file...");
  const API_ENDPOINT = url; // "http://localhost:8080/extrator/api/v1/comprovante"; //http://samsung-ocr-hom.oobj-dfe.com.br/extrator/api/v1/comprovante
  const request = new XMLHttpRequest();

  const formData = new FormData();
  formData.append("file", file);
  formData.append("requestid", requestid);
  formData.append("webhookurl", webhook);

  request.open("POST", API_ENDPOINT, true);
  request.onreadystatechange = () => {
    if (request.readyState === 3 && (request.status === 202 || request.status === 200)) {
      console.log(request.responseText);
      document.getElementById("result").innerText = request.response;
    }
  };
  
  request.setRequestHeader("Authorization","Bearer " + token);
  request.send(formData);

};

const form = document.getElementById("form");
const inputFile = document.getElementById("file");

const handleSubmit = (event) => {
    event.preventDefault();

    const url = document.getElementById("url").value;
    const token = document.getElementById("token").value;

    const webhook = document.getElementById("webhook").value;
    const requestid = document.getElementById("requestid").value;

    const files = event.target.files;
    uploadFile(inputFile.files[0], url, token, webhook, requestid);
    
};

form.addEventListener("submit", handleSubmit);