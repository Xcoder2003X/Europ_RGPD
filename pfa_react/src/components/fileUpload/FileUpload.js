import "./fileUpload.css";
import React, { useState } from "react";
import axios from "axios";
import {
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TableContainer,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import { makeStyles } from "@mui/styles";
import { toast } from "react-toastify";

// 🎨 Styles pour le tableau

// Add this utility component at the top of your file
const RenderValue = ({ value }) => {
  if (typeof value === "object" && value !== null) {
    return (
      <ul style={{ margin: 0, paddingLeft: "20px", listStyleType: "none" }}>
        {Object.entries(value).map(([subKey, subVal]) => (
          <li key={subKey}>
            <strong>{subKey.replace(/_/g, " ")}:</strong>{" "}
            <RenderValue value={subVal} />
          </li>
        ))}
      </ul>
    );
  }
  if (Array.isArray(value)) {
    return (
      <ul style={{ margin: 0, paddingLeft: "20px" }}>
        {value.map((item, index) => (
          <li key={index}>
            <RenderValue value={item} />
          </li>
        ))}
      </ul>
    );
  }
  return <span>{value?.toString()}</span>;
};

const FileUpload = () => {
  const [file, setFile] = useState(null);
  const [analysisData, setAnalysisData] = useState(null);
  const [message, setMessage] = useState("");
  const [reportUrl, setReportUrl] = useState("");
  const [open, setOpen] = useState(false);
  // Add new state variables

  const [conformityDialogOpen, setConformityDialogOpen] = useState(false);
  const [conformityPoints, setConformityPoints] = useState([]);
  const [nonConformityPoints, setNonConformityPoints] = useState([]);
  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      toast.error("❌ Veuillez sélectionner un fichier.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const token = localStorage.getItem("token");

      const response = await axios.post(
        "http://localhost:8080/api/reports/analyze",

        formData,
        {
          headers: {
            // on laisse axios générer le bon Content‑Type avec boundary
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const { rgpd_analysis } = response.data;

      if (rgpd_analysis) {
        setConformityPoints(rgpd_analysis.points_conformite || []);
        setNonConformityPoints(rgpd_analysis.points_non_conformite || []);
        setConformityDialogOpen(true);
      }

      setAnalysisData(response.data);
    } catch (error) {
      toast.error("❌ Erreur lors de l'analyse du fichier !");
    }
  };

  // 🔹 Génération et téléchargement du rapport PDF
  const downloadReport = async () => {
    if (!file) {
      alert("❌ Veuillez d'abord sélectionner et analyser un fichier !");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/files/generate-report",
        formData,
        {
          responseType: "blob",
        }
      );

      // Check if the response is an error message
      if (
        response.data instanceof Blob &&
        response.data.type === "application/json"
      ) {
        const errorText = await response.data.text();
        const errorJson = JSON.parse(errorText);
        throw new Error(errorJson.error || "Erreur inconnue");
      }

      // Create download link
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      setReportUrl(url);

      // Open dialog after successful generation
      setOpen(true);
    } catch (error) {
      console.error("Error generating report:", error);
      alert(`❌ Échec de la génération du rapport: ${error.message}`);
    }
  };

  const ConformityDialog = () => (
    <Dialog
      open={conformityDialogOpen}
      onClose={() => setConformityDialogOpen(false)}
      maxWidth="md"
      color="white"
      fullWidth
    >
      <DialogTitle
        style={{
          background: "#2d2d2d",
          color: "white",
          borderBottom: "2px solid #444",
        }}
      >
        Analyse de conformité RGPD
      </DialogTitle>

      <DialogContent
        style={{
          background: "#1a1a1a",
          padding: "20px",
          minHeight: "400px",
          color: "white",
        }}
      >
        {/* Section Non-conformité */}
        {nonConformityPoints.length > 0 && (
          <div style={{ marginBottom: "30px" }}>
            <h3
              style={{
                color: "#ff4444",
                marginBottom: "15px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <span
                style={{
                  background: "#ff4444",
                  width: "10px",
                  height: "10px",
                  borderRadius: "50%",
                  marginRight: "10px",
                }}
              ></span>
              Points de non-conformité ({nonConformityPoints.length})
            </h3>
            <ul
              style={{
                listStyle: "none",
                padding: 0,
                margin: 0,
                borderLeft: "3px solid #ff4444",
                paddingLeft: "15px",
              }}
            >
              {nonConformityPoints.map((point, index) => (
                <li
                  key={`non-conform-${index}`}
                  style={{
                    background: "rgba(255, 68, 68, 0.1)",
                    padding: "12px",
                    margin: "8px 0",
                    borderRadius: "6px",
                    display: "flex",
                    alignItems: "baseline",
                  }}
                >
                  <span
                    style={{
                      color: "#ff4444",
                      marginRight: "10px",
                      fontWeight: "bold",
                    }}
                  >
                    ✖
                  </span>
                  {point}
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Section Conformité */}
        {conformityPoints.length > 0 && (
          <div>
            <h3
              style={{
                color: "#00C851",
                marginBottom: "15px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <span
                style={{
                  background: "#00C851",
                  width: "10px",
                  height: "10px",
                  borderRadius: "50%",
                  marginRight: "10px",
                }}
              ></span>
              Points conformes ({conformityPoints.length})
            </h3>
            <ul
              style={{
                listStyle: "none",
                padding: 0,
                margin: 0,
                borderLeft: "3px solid #00C851",
                paddingLeft: "15px",
              }}
            >
              {conformityPoints.map((point, index) => (
                <li
                  key={`conform-${index}`}
                  style={{
                    background: "rgba(0, 200, 81, 0.1)",
                    padding: "12px",
                    margin: "8px 0",
                    borderRadius: "6px",
                    display: "flex",
                    alignItems: "baseline",
                  }}
                >
                  <span
                    style={{
                      color: "#00C851",
                      marginRight: "10px",
                      fontWeight: "bold",
                    }}
                  >
                    ✔
                  </span>
                  {point}
                </li>
              ))}
            </ul>
          </div>
        )}
      </DialogContent>

      <DialogActions
        style={{
          background: "#2d2d2d",
          padding: "15px 20px",
          borderTop: "2px solid #444",
        }}
      >
        <Button
          onClick={() => setConformityDialogOpen(false)}
          style={{
            color: "#fff",
            background: "#555",
            "&:hover": { background: "#666" },
          }}
        >
          Fermer
        </Button>
      </DialogActions>
    </Dialog>
  );

  return (
    <div className="file-upload-container">
      <h2 className="upload-title">Upload de fichiers</h2>

      <div className="file-upload-content">
        <input type="file" onChange={handleFileChange} />
        <button className="button-analyse" onClick={handleUpload}>
          Analyser le fichier
        </button>
      </div>

      {message && <p className="upload-message">{message}</p>}

      <div>
        <button className="button-rapport" onClick={downloadReport}>
          Générer le Rapport
        </button>
      </div>
      <ConformityDialog />
      {analysisData?.rgpd_analysis && (
        <p
          style={{
            color: analysisData.rgpd_analysis.consentement_valide
              ? "green"
              : "red",
            fontWeight: "bold",
            margin: "10px 0",
          }}
        >
          {analysisData.rgpd_analysis.consentement_valide
            ? "✅ Consentement valide"
            : "⛔ Consentement non valide"}
        </p>
      )}
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Rapport Généré</DialogTitle>
        <DialogContent>
          <p>Le rapport a été généré avec succès.</p>
          <p>Vous pouvez le télécharger ci-dessous :</p>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)} color="secondary">
            Fermer
          </Button>
          <Button href={reportUrl} download="report.pdf" color="primary">
            Télécharger
          </Button>
        </DialogActions>
      </Dialog>

      {/* 🔹 Tableau stylisé avec un scroll */}
      {analysisData?.error && (
        <div className="error-message">Error: {analysisData.error}</div>
      )}
    </div>
  );
};

export default FileUpload;
