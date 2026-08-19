import { Download, Eye } from "lucide-react";
import { usePaperRequest } from "./hook/usePaperRequest";
import style from "./ResearchModal.module.css";
import { downloadFile, viewFileInTab } from "@/api/files";
import { Button } from "@/components/common/Button/Button";
import { Dialog, DialogClose, DialogContent, DialogTitle } from "@/components/common/Dialog/Dialog";
import { useAuth } from "@/features/auth/context/useAuth";
import type { ResearchPaper } from "@/types";
import { triggerBrowserDownload } from "@/util/download";
import { formatDateLong } from "@/util/formatDate";
import { isUserFaculty, isUserStudent, isUserSuperOrDepartmentAdmin } from "@/util/roleBasedAccess";

interface ResearchModalProps {
  isOpen: boolean;
  paper: ResearchPaper;
  onClose: () => void;
}

export const ResearchModal = ({ isOpen, paper, onClose }: ResearchModalProps) => {
  const { user } = useAuth();
  const { requestExists, isRequestLoading, requestDocument } = usePaperRequest(paper.paperId, user);

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      onClose();
    }
  };

  const formattedDate = formatDateLong(paper.submissionDate);
  const department = paper.department.departmentName;

  const handleView = () => {
    if (!paper.paperId) return;
    void viewFileInTab(paper.paperId);
  };

  const handleDownload = () => {
    if (!paper.paperId) return;
    void downloadFile(paper.paperId).then(({ blob, filename }) => {
      triggerBrowserDownload(blob, filename);
    });
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange} title={paper.title}>
      <DialogContent className={style.modal} aria-describedby={undefined}>
        <DialogClose onClose={onClose} />
        <div className={style.infoWrapper}>
          <DialogTitle className={style.title}>{paper.title}</DialogTitle>
          <div className={style.authordateWrapper}>
            <p className={style.author}>{paper.authorName}</p>
            <p className={style.date}>{formattedDate}</p>
          </div>
        </div>

        <div className={style.departmentArchivedContainer}>
          <div className={style.departmentContainer}>
            <p className={style.department}>{department}</p>
          </div>
          {paper.archived && (
            <div className={style.archivedContainer}>
              <p className={style.archived}>Archived</p>
            </div>
          )}
        </div>

        <div className={style.abstractWrapper}>
          <h3 className={style.abtractHeader}>Abstract</h3>
          <p className={style.abstractText}>{paper.abstractText}</p>
        </div>

        {(isUserSuperOrDepartmentAdmin(user) ||
          (paper.uploadedBy?.userId != null && paper.uploadedBy.userId === user?.userId)) && (
          <div className={style.actionsRow}>
            {isUserSuperOrDepartmentAdmin(user) && (
              <Button onPress={handleDownload} variant="primary">
                <Download className={style.actionIcon} />
                Download
              </Button>
            )}
            <Button onPress={handleView} variant="secondary">
              <Eye className={style.actionIcon} />
              View Paper
            </Button>
          </div>
        )}

        {(isUserStudent(user) || isUserFaculty(user)) &&
          !paper.archived &&
          paper.status === "ACTIVE" &&
          paper.uploadedBy?.userId !== user?.userId && (
            <Button
              onPress={requestDocument}
              isDisabled={requestExists}
              isPending={isRequestLoading}
              variant="primary"
            >
              {requestExists ? "Request Submitted" : "Request Document"}
            </Button>
          )}
      </DialogContent>
    </Dialog>
  );
};
