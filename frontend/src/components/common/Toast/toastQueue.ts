import { UNSTABLE_ToastQueue as ToastQueue } from "react-aria-components/Toast";

export interface ToastData {
  variant: "success" | "error" | "normal";
  title: string;
  description?: string;
}

export const toastQueue = new ToastQueue<ToastData>({
  maxVisibleToasts: 5,
});
