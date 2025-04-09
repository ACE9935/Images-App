import { Timestamp } from "firebase/firestore";

function parseFirebaseDateValue(date: any, includeTime?: boolean): string {
  let dt: Date;

  // Check if date is an instance of Firebase Timestamp or has a "seconds" property.
  if (date instanceof Timestamp) {
    dt = date.toDate();
  } else if (date && typeof date.seconds === "number") {
    dt = new Date(date.seconds * 1000);
  } else {
    dt = new Date();
  }

  if (includeTime) {
    // Include date and time (hour and minute)
    return dt.toLocaleString("en-US", {
      dateStyle: "medium",
      timeStyle: "short",
    }).replace(",", "");
  } else {
    // Return only the date
    return dt.toLocaleDateString("en-US", {
      dateStyle: "medium",
    }).replace(",", "");
  }
}

export default parseFirebaseDateValue;
