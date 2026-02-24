/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        orbitron: ['Orbitron', 'sans-serif'],
        inter: ['Inter', 'sans-serif'],
      },
      colors: {
        space: {
          900: '#03001C',
          800: '#0B0B2A',
          700: '#13113C',
          600: '#1B1464',
          500: '#2E22A0',
        },
        primary: {
          50: '#e0fbfc',
          100: '#c2f7f9',
          200: '#93eff3',
          300: '#56e1e8',
          400: '#23ccd4',
          500: '#00b4d8',
          600: '#0090b5',
          700: '#007394',
          800: '#005f7a',
          900: '#004f66',
        },
        neon: {
          cyan: '#00F0FF',
          magenta: '#FF003C',
          purple: '#B026FF',
          green: '#00FF66',
        }
      },
      backgroundImage: {
        'space-gradient': 'linear-gradient(to right bottom, #03001C, #0B0B2A, #13113C)',
        'glass-gradient': 'linear-gradient(135deg, rgba(255,255,255,0.05) 0%, rgba(255,255,255,0.01) 100%)',
        'hologram-gradient': 'linear-gradient(135deg, #00F0FF, #B026FF, #FF003C, #00FF66)',
      },
      boxShadow: {
        'neon-cyan': '0 0 10px rgba(0, 240, 255, 0.5), 0 0 20px rgba(0, 240, 255, 0.3)',
        'neon-purple': '0 0 10px rgba(176, 38, 255, 0.5), 0 0 20px rgba(176, 38, 255, 0.3)',
        'neon-magenta': '0 0 10px rgba(255, 0, 60, 0.5), 0 0 20px rgba(255, 0, 60, 0.3)',
        'glass': '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
        'holographic': '0 0 15px rgba(0,240,255,0.2), 0 0 30px rgba(176,38,255,0.1), 0 0 45px rgba(255,0,60,0.05)',
        'neon-intense': '0 0 20px rgba(0,240,255,0.6), 0 0 40px rgba(0,240,255,0.3), 0 0 60px rgba(0,240,255,0.1)',
      },
      animation: {
        'float': 'float 6s ease-in-out infinite',
        'pulse-glow': 'pulse-glow 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'spin-slow': 'spin 8s linear infinite',
        'spin-slower': 'spin 20s linear infinite',
        'scan': 'scanline 2s linear infinite',
        'glitch': 'glitch 0.3s ease-in-out infinite alternate',
        'glitch-color': 'glitch-color 2s ease-in-out infinite',
        'radar': 'radar-sweep 4s linear infinite',
        'orbital': 'orbital-spin 20s linear infinite',
        'orbital-reverse': 'orbital-spin-reverse 25s linear infinite',
        'flicker': 'text-flicker 3s step-end infinite',
        'warp': 'warp-speed 0.6s ease-in-out',
        'energy': 'energy-charge 2s ease-in-out infinite',
        'border-pulse': 'border-pulse 2s ease-in-out infinite',
        'float-astro': 'float-astronaut 8s ease-in-out infinite',
        'hologram': 'hologram-shift 3s ease-in-out infinite',
        'pulse-ring': 'pulse-ring 2s ease-out infinite',
        'count-glow': 'count-up-glow 2s ease-in-out infinite',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        'pulse-glow': {
          '0%, 100%': { opacity: 1, filter: 'brightness(1)' },
          '50%': { opacity: .7, filter: 'brightness(1.5)' },
        }
      }
    },
  },
  plugins: [],
};
