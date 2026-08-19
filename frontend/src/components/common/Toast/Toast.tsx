import { X } from "lucide-react";
import { useState } from "react";
import {
  Button,
  Text,
  UNSTABLE_Toast as Toast,
  UNSTABLE_ToastContent as ToastContent,
  type ToastProps,
  UNSTABLE_ToastRegion as ToastRegion,
} from "react-aria-components/Toast";
import styles from "./Toast.module.css";
import { type ToastData, toastQueue } from "./toastQueue";

export function ToastRegionProvider() {
  return (
    <ToastRegion queue={toastQueue} className={styles.region}>
      {({ toast }) => <AppToast toast={toast} />}
    </ToastRegion>
  );
}

function AppToast({ toast }: ToastProps<ToastData>) {
  const { variant, title, description } = toast.content;
  const variantClass = styles[variant];
  const [isExiting, setIsExiting] = useState(false);

  return (
    <Toast
      toast={toast}
      className={`${styles.toast} ${variantClass} ${isExiting ? styles.exiting : ""}`}
      onClick={() => {
        setIsExiting(true);
      }}
      onAnimationEnd={() => {
        if (isExiting) toast.onClose?.();
      }}
    >
      <ToastContent className={styles.content}>
        <Text slot="title" className={styles.title}>
          {title}
        </Text>
        {description && (
          <Text slot="description" className={styles.description}>
            {description}
          </Text>
        )}
      </ToastContent>
      <Button
        onPress={() => {
          setIsExiting(true);
        }}
        className={styles.closeButton}
        aria-label="Close"
      >
        <X size={16} />
      </Button>
    </Toast>
  );
}
