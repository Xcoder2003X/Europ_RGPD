import React from 'react';
import { Box, Typography, Paper, Button, keyframes } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import AssessmentIcon from '@mui/icons-material/Assessment';

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
  margin: '8px 0',
  borderRadius: '10px',
  background: 'linear-gradient(145deg, #f6f9ff, #e3f2fd)',
  animation: `${fadeInUp} 1.5s ease-out`,
  animationFillMode: 'both',
  animationDelay: `${delay}s`,
  transition: 'transform 0.2s',
  '&:hover': {
    transform: 'translateX(10px)'
  }
}));

const StepNumber = styled(Box)(({ theme }) => ({
  width: '25px',
  height: '25px',
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
  0% { transform: translateY(0px) rotateY(-5deg); }
  50% { transform: translateY(-5px) rotateY(5deg); }
  100% { transform: translateY(0px) rotateY(-5deg); }
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
  boxShadow: '0 8px 10px rgba(153, 152, 153, 0.95)',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  
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
  },
  
 
}));

const StyledButton = styled(Button)(({ theme }) => ({
  background: 'linear-gradient(45deg, #0088cc 30%, #006699 90%)',
  color: '#fff',
  padding: '16px 40px',
  fontSize: '1.1rem',
  marginTop: '30px',
  borderRadius: '50px',
  fontWeight: '600',
  textTransform: 'none',
  transition: 'transform 0.2s',
  '&:hover': {
    transform: 'scale(1.05)',
    background: 'linear-gradient(45deg, #006699 30%, #004466 90%)'
  }
}));

const FirstSteps = () => {
  const navigate = useNavigate();

  const handleStart = () => {
    navigate('/upload');
  };

  const steps = [
    "Sélectionner votre fichier",
    "Analyse automatique de vos données",
    "Détection des non-conformités RGPD",
    "Rapport détaillé avec recommandations"
  ];

  return (
    <Box sx={{ 
      width: '100%', 
      padding: '40px 20px',
      maxHeight: '80vh',
      display: 'flex',
      alignItems: 'bottom',
      justifyContent: 'center',
      
    }}>
      <StyledPaper elevation={3}>
        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <AssessmentIcon sx={{ 
            fontSize: 60, 
            color: '#0088cc',
            filter: 'drop-shadow(0 4px 8px rgba(0, 136, 204, 0.2))'
          }} />
          
          <Typography 
            variant="h3" 
            component="h1" 
            sx={{ 
              margin: '5px 0 10px',
              color: '#002233',
              fontWeight: '800',
              fontSize: '1.9rem',
              letterSpacing: '-0.5px'
            }}
          >
            RGPD Compliance Analysis
          </Typography>

          <Typography 
            variant="h6" 
            sx={{ 
              color: '#556677',
              fontWeight: '400',
              fontSize: '1rem'
            }}
          >
            Évaluez votre conformité RGPD en quelques étapes simples
          </Typography>
        </Box>

        <Box sx={{ 
          display: 'flex', 
          flexDirection: 'column', 
          gap: '10px',
          maxWidth: '600px',
          margin: '0 auto',
          mb: 4
        }}>
          {steps.map((step, index) => (
            <StyledStep key={index} delay={index * 0.2}>
              <StepNumber>{index + 1}</StepNumber>
              <Typography variant="body1" sx={{ 
                color: '#334455', 
                fontSize: '1rem',
                fontWeight: '500'
              }}>
                {step}
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