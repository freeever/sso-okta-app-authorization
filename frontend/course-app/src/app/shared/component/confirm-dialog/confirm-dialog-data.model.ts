export interface ConfirmDialogData {
  title: string;
  contentHtml: string;
  confirmButtonLabel?: string;
  cancelButtonLabel?: string;
  showCancelButton?: boolean;
  onConfirm?: () => void;
  onCancel?: () => void;
}
