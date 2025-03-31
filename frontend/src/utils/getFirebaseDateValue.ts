import { Timestamp } from "firebase/firestore";

function getFirebaseDateValue(date:any) {

    const timestamp = date instanceof Timestamp ? date.toDate() : new Date();
    const formattedDate = timestamp.toLocaleString("en-US", {
      dateStyle: "medium",
      timeStyle: "short",
    });

    return formattedDate;
}

export default getFirebaseDateValue;