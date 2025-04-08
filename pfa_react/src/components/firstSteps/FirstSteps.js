import React from 'react';
import { Box, Typography, Paper, Button, keyframes } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import AssessmentIcon from '@mui/icons-material/Assessment';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import AutoGraphIcon from '@mui/icons-material/AutoGraph';
import WarningIcon from '@mui/icons-material/Warning';
import DescriptionIcon from '@mui/icons-material/Description';

const fadeInUp = keyframes`
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const StyledStep = styled(Box)(({ theme, delay }) => ({
  display: 'flex',
  alignItems: 'center',
  gap: '10px',
  padding: '15px 20px',
  margin: '3.5px 0',
  borderRadius: '10px',
  background: 'linear-gradient(145deg,rgb(218, 219, 220), #e3f2fd)',
  animation: `${fadeInUp} 3s ease-out`,
  animationFillMode: 'both',
  animationDelay: `${delay}s`,
  transition: 'transform 0.2s',
  '&:hover': {
    transform: 'translateX(10px)'
  }
}));

const StepNumber = styled(Box)(() => ({
  width: '30px',
  height: '30px',
  borderRadius: '50%',
  background: '#0088cc',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: 'white',
  fontWeight: 'bold',
  flexShrink: 0
}));

const floatAnimation = keyframes`
  0% { transform: translateY(-2px) rotateY(-5deg); }
  50% { transform: translateY(-4px) rotateY(5deg); }
  100% { transform: translateY(-2px) rotateY(-5deg); }
`;

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: '30px 20px',
  borderRadius: '20px',
  background: 'white',
  width: '700px',
  height: '550px',
  margin: '0 auto',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  transform: 'perspective(1000px) rotateY(-5deg)',
  animation: `${floatAnimation} 4s ease-in-out infinite`,
  position: 'relative',
  boxShadow: '0 5px 6px rgb(78, 114, 146)',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  overflow: 'hidden',
  '&:before': {
    content: '""',
    position: 'absolute',
    bottom: '-30px',
    left: '5%',
    right: '5%',
    height: '20px',
    background: 'rgba(0, 0, 0, 0.1)',
    borderRadius: '50%',
    filter: 'blur(10px)',
    zIndex: -1
  }
}));

const StyledButton = styled(Button)(() => ({
  background: 'linear-gradient(45deg, #0088cc 30%, #006699 90%)',
  color: '#fff',
  padding: '12px 40px',
  fontSize: '1.1rem',
  marginTop: '0px',
  borderRadius: '50px',
  fontWeight: '600',
  textTransform: 'none',
  transition: 'transform 0.2s',
  '&:hover': {
    transform: 'scale(1.05)',
    background: 'linear-gradient(45deg, #006699 30%, #004466 90%)'
  }
}));

const BackgroundSVG = styled('div')({
  position: 'absolute',
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  zIndex: -2,
  background: 'linear-gradient(120deg, #e0f7fa 0%, #ffffff 100%)',
  '& svg': {
    position: 'absolute',
    opacity: 0.1
  }
});

const FirstSteps = () => {
  const navigate = useNavigate();

  const handleStart = () => {
    navigate('/upload');
  };

  const steps = [
    { label: "Sélectionner votre fichier", icon: <InsertDriveFileIcon sx={{ color: '#0088cc' }} /> },
    { label: "Analyse automatique de vos données", icon: <AutoGraphIcon sx={{ color: '#0088cc' }} /> },
    { label: "Détection des non-conformités RGPD", icon: <WarningIcon sx={{ color: '#0088cc' }} /> },
    { label: "Rapport détaillé avec recommandations", icon: <DescriptionIcon sx={{ color: '#0088cc' }} /> }
  ];

  return (
    <Box sx={{ width: '100%', padding: '40px 20px', display: 'flex', justifyContent: 'center' }}>
      <StyledPaper elevation={3}>
        <BackgroundSVG>
          <svg width="300" height="300">
            <circle cx="150" cy="150" r="100" fill="#0088cc" />
          </svg>
        </BackgroundSVG>

        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <AssessmentIcon sx={{ fontSize: 60, color: '#0088cc', filter: 'drop-shadow(0 4px 8px rgba(0, 136, 204, 0.2))' }} />
          <Typography variant="h3" component="h1" sx={{ margin: '5px 0 10px', color: '#002233', fontWeight: '800', fontSize: '1.9rem', letterSpacing: '-0.5px' }}>
            RGPD Compliance Analysis
          </Typography>
          <Typography variant="h6" sx={{ color: '#556677', fontWeight: '400', fontSize: '1rem' }}>
            Évaluez votre conformité RGPD en quelques étapes simples
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '10px', maxWidth: '600px', margin: '0 auto', mb: 4 }}>
          {steps.map((step, index) => (
            <StyledStep key={index} delay={index * 0.2}>
              <StepNumber>{index + 1}</StepNumber>
              {step.icon}
              <Typography variant="body1" sx={{ color: '#334455', fontSize: '1rem', fontWeight: '500' }}>
                {step.label}
              </Typography>
            </StyledStep>
          ))}
        </Box>

        <Box sx={{ textAlign: 'center' }}>
          <StyledButton
            variant="contained"
            onClick={handleStart}
            startIcon={<AssessmentIcon sx={{ color: 'white' }} />}
          >
            Démarrer l'analyse gratuite
          </StyledButton>
        </Box>
      </StyledPaper>
    </Box>
  );
};

export default FirstSteps;
