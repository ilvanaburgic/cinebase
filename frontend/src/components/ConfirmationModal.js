import styles from "./ConfirmationModal.module.css";

export default function ConfirmationModal({ message, onClose, type = "success", onConfirm, confirmText = "OK", cancelText = "Cancel" }) {
    const isConfirm = type === "confirm";

    return (
        <div className={styles.overlay} onClick={isConfirm ? undefined : onClose}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <div className={styles.iconWrapper}>
                    {type === "success" ? (
                        <svg className={styles.checkIcon} viewBox="0 0 52 52">
                            <circle className={styles.checkCircle} cx="26" cy="26" r="25" fill="none"/>
                            <path className={styles.checkPath} fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
                        </svg>
                    ) : (
                        <div className={styles.questionIcon}>?</div>
                    )}
                </div>
                <h2 className={styles.title}>{type === "success" ? "Success!" : "Confirm Action"}</h2>
                <p className={styles.message}>{message}</p>

                {isConfirm ? (
                    <div className={styles.buttonGroup}>
                        <button className={styles.cancelButton} onClick={onClose}>
                            {cancelText}
                        </button>
                        <button className={styles.confirmButton} onClick={onConfirm}>
                            {confirmText}
                        </button>
                    </div>
                ) : (
                    <button className={styles.okButton} onClick={onClose}>
                        OK
                    </button>
                )}
            </div>
        </div>
    );
}
