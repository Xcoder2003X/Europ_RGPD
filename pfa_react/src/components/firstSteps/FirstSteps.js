import React from 'react';
import { Box, Typography, Paper, Button, keyframes } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import AssessmentIcon from '@mui/icons-material/Assessment';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import AutoGraphIcon from '@mui/icons-material/AutoGraph';
import WarningIcon from '@mui/icons-material/Warning';
import DescriptionIcon from '@mui/icons-material/Description';
import { useTranslation } from "react-i18next";
import "./../../i18n"; // Ensure i18n is initialized

const fadeInUp = keyframes`
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const floatY = keyframes`
  0% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
  100% { transform: translateY(0); }
`;

const StyledStep = styled(Box)(({ theme, delay }) => ({
  display: 'flex',
  alignItems: 'center',
  gap: '10px',
  padding: '15px 20px',
  margin: '5px 0',
  borderRadius: '12px',
  background: 'rgba(255, 255, 255, 0.15)',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255, 255, 255, 0.2)',
  animation: `${fadeInUp} 1s ease-out forwards`,
  animationDelay: `${delay}s`,
  transition: 'all 0.3s ease',
  '&:hover': {
    background: 'rgba(255, 255, 255, 0.25)',
    boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.37)',
    transform: 'scale(1.02)'
  }
}));

const StepNumber = styled(Box)(() => ({
  width: '32px',
  height: '32px',
  borderRadius: '50%',
  backgroundColor: '#00c3ff',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: 'white',
  fontWeight: 'bold',
  flexShrink: 0,
  boxShadow: '0 4px 10px rgba(0, 195, 255, 0.5)'
}));

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: '40px 30px',
  borderRadius: '25px',
  background: 'rgba(255, 255, 255, 0.1)',

  backdropFilter: 'blur(20px)',
  border: '1px solid rgba(255, 255, 255, 0.2)',
  width: '720px',
  height: '550px',
  display: 'flex',
  marginTop: '80px',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  animation: `${floatY} 5s ease-in-out infinite`,
  boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.37)',
  overflow: 'hidden',
}));


const StyledButton = styled(Button)(() => ({
  background: 'linear-gradient(135deg, #00c3ff 0%, #004aad 100%)',
  color: '#fff',
  padding: '14px 50px',
  fontSize: '1.2rem',
  marginTop: '25px',
  borderRadius: '50px',
  fontWeight: 'bold',
  textTransform: 'none',
  boxShadow: '0 8px 16px rgba(0, 195, 255, 0.4)',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    transform: 'scale(1.08)',
    boxShadow: '0 12px 24px rgba(0, 195, 255, 0.6)'
  }
}));

const ParallaxBackground = styled('div')({
  position: 'absolute',
  top: 0,
  left: 0,
  width: '100%',
  height: '200%',
  backgroundSize: 'cover',
  animation: 'moveBackground 30s linear infinite',
  zIndex: -1,
  '@keyframes moveBackground': {
    '0%': { backgroundPosition: '0% 50%' },
    '50%': { backgroundPosition: '100% 50%' },
    '100%': { backgroundPosition: '0% 50%' }
  }
});

const FirstSteps = () => {

  // Importing the useTranslation hook from react-i18next for internationalization
  const { t } = useTranslation();
  
  const navigate = useNavigate();

  const handleStart = () => {
    navigate('/upload');
  };

  const steps = [
    { label: `${t('firstSteps.step1')}`, icon: <InsertDriveFileIcon sx={{ color: '#00c3ff' }} /> },
    { label: `${t('firstSteps.step2')}`, icon: <AutoGraphIcon sx={{ color: '#00c3ff' }} /> },
    { label: `${t('firstSteps.step3')}`, icon: <WarningIcon sx={{ color: '#00c3ff' }} /> },
    { label: `${t('firstSteps.step4')}`, icon: <DescriptionIcon sx={{ color: '#00c3ff' }} /> }
  ];

  return (
<Box 
  sx={{
    width: '100%',
    minHeight: '100vh',
    padding: '40px 20px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
    overflow: 'hidden',
    flexDirection: 'column' // important pour empiler verticalement
  }}
>      <ParallaxBackground />
      
      <StyledPaper elevation={5}>
        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <Typography variant="h3" component="h1" sx={{ color: '#ffffff', fontWeight: '800', fontSize: '2.5rem', mb: 1 }}>
            {t('firstSteps.title')}
          </Typography>
          <Typography variant="h6" sx={{ color: '#d0d0d0', fontSize: '1.1rem' }}>
          {t('firstSteps.title1')}
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '12px', maxWidth: '600px', width: '100%' }}>
          {steps.map((step, index) => (
            <StyledStep key={index} delay={index * 0.4}>
              <StepNumber>{index + 1}</StepNumber>
              {step.icon}
              <Typography variant="body1" sx={{ color: '#ffffff', fontWeight: '500' }}>
                {step.label}
              </Typography>
            </StyledStep>
          ))}
        </Box>

        <StyledButton
          variant="contained"
          onClick={handleStart}
          startIcon={<AssessmentIcon sx={{ color: 'white' }} />}
        >
          {t('firstSteps.getStartedButton')}
        </StyledButton>
      </StyledPaper>
    </Box>
  );
};

export default FirstSteps;
