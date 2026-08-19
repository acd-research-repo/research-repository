import { createContext } from "react";

export interface NotificationContextValue {
  unreadCount: number;
  markAllRead: () => Promise<void>;
  markAsRead: (notificationId: number, wasUnread: boolean) => Promise<void>;
}

export const NotificationContext = createContext<NotificationContextValue | undefined>(undefined);
