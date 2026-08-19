import { useContext } from "react";
import { NotificationContext, type NotificationContextValue } from "./NotificationContext";

export function useNotificationContext(): NotificationContextValue {
  const context = useContext(NotificationContext);
  if (context == null) {
    throw new Error("useNotificationContext must be used within a NotificationProvider");
  }
  return context;
}
