import React, {useState} from "react";
import "antd/dist/antd.css";
import {Button, Card, Progress, Row} from "antd";

function ProgressBarComponent() {
  const [fetching, setFetching] = useState(false);
  const [selectedFile, setFiles] = useState(undefined);
  const [uploadPercentage, setUploadPercentage] = useState(0);

  const handleSelecteFile = (event) => {
    console.log(event);
    // setFiles(event.target.files[0]);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log(event.target.to_account);
    debugger
    const formData = new FormData();
    // formData.append("from_account", );
    console.log(formData);
    const data = JSON.stringify({
      from_account: `${event.target.from_account.value}`,
      to_account: `${event.target.to_account.value}`,
      amount: `${event.target.amount.value}`
    });

    let url = "http://localhost:8080/send_money";
    const eventSource = new EventSource("http://localhost:8080/send_money_progress");
    let guidValue = null;

    eventSource.addEventListener("GUI_ID", (event) => {
      guidValue = JSON.parse(event.data);
      console.log(`Guid from server: ${guidValue}`);
      // data.append("guid", guidValue);
      url += "?guid=" + guidValue;
      eventSource.addEventListener(guidValue, (event) => {
        const result = JSON.parse(event.data);
        if (uploadPercentage !== result) {
          setUploadPercentage(result);
        }
        if (result === "100") {
          eventSource.close();
        }
      });
      uploadToServer(url, data);
    });

    eventSource.onerror = (event) => {
      if (event.target.readyState === EventSource.CLOSED) {
        console.log("SSE closed (" + event.target.readyState + ")");
      }
      setUploadPercentage(0);
      eventSource.close();
    };

    eventSource.onopen = () => {
      console.log("connection opened");
    };
  };

  const uploadToServer = (url, data) => {
    setFetching(true);
    console.log("Send Money");
    console.log(data);

    const requestOptions = {
      method: "POST",
      mode: "no-cors",
      body: data,
    };
    fetch(url, requestOptions).then((res) => console.log(res));
  };

  return (
    <div>
      <Card title="Send Money Indicator">
        <Row justify="center">
          <Progress type="circle" percent={(uploadPercentage / 100) * 100} />
        </Row>
        <br></br>
        <Row justify="center">
          {fetching &&
            (uploadPercentage / 100) * 100 !== 100 &&
            `Sending Money progress [${(uploadPercentage / 100) * 100}/100]%`}
          {(uploadPercentage / 100) * 100 === 100 &&
            "Money has been sent Successfully"}
        </Row>
        <br />
        <Row justify="center">
          <form>
            <label>
              From Account: <input type="text" name= "from_account" />
            </label>
            <label>
              To Account: <input type="text" name= "to_account" />
            </label>
            <label>
              Total Amount: <input type="text" name= "amount"/>
            </label>

            <Button
              type="primary submit"
              onClick={handleSubmit}
            >
              Send Money
            </Button>
          </form>
        </Row>
      </Card>
    </div>
  );
}

export default ProgressBarComponent;
