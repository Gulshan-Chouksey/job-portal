import { Outlet } from 'react-router-dom';
import { useEffect, useRef } from 'react';
import Navbar from './Navbar';
import StarfieldCanvas from './StarfieldCanvas';
import ParticleField from './ParticleField';

export default function Layout() {
  const cursorRef = useRef(null);
  const dotRef = useRef(null);

  useEffect(() => {
    const cursor = cursorRef.current;
    const dot = dotRef.current;
    if (!cursor || !dot) return;

    let cx = 0, cy = 0, dx = 0, dy = 0;

    const move = (e) => {
      dx = e.clientX;
      dy = e.clientY;
      dot.style.left = dx + 'px';
      dot.style.top = dy + 'px';
    };

    const animate = () => {
      cx += (dx - cx) * 0.15;
      cy += (dy - cy) * 0.15;
      cursor.style.left = cx + 'px';
      cursor.style.top = cy + 'px';
      requestAnimationFrame(animate);
    };

    window.addEventListener('mousemove', move);
    animate();

    // Grow cursor on interactive elements
    const grow = () => { cursor.style.width = '40px'; cursor.style.height = '40px'; cursor.style.borderColor = 'rgba(176,38,255,0.8)'; };
    const shrink = () => { cursor.style.width = '20px'; cursor.style.height = '20px'; cursor.style.borderColor = 'rgba(0,240,255,0.8)'; };

    const observe = () => {
      document.querySelectorAll('a, button, input, textarea, select, [role="button"]').forEach(el => {
        el.addEventListener('mouseenter', grow);
        el.addEventListener('mouseleave', shrink);
      });
    };
    observe();
    const observer = new MutationObserver(observe);
    observer.observe(document.body, { childList: true, subtree: true });

    return () => {
      window.removeEventListener('mousemove', move);
      observer.disconnect();
    };
  }, []);

  return (
    <div className="min-h-screen flex flex-col relative overflow-hidden">
      {/* Animated starfield background */}
      <StarfieldCanvas />
      <ParticleField />

      {/* Custom cursor */}
      <div ref={cursorRef} className="cursor-glow hidden md:block" />
      <div ref={dotRef} className="cursor-dot hidden md:block" />

      {/* Orbital rings (decorative) */}
      <div className="orbit-ring hidden lg:block" style={{ width: '600px', height: '600px', top: '-200px', right: '-200px', '--orbit-duration': '30s' }} />
      <div className="orbit-ring orbit-ring-reverse hidden lg:block" style={{ width: '400px', height: '400px', top: '-100px', right: '-100px', '--orbit-duration': '22s', borderColor: 'rgba(176,38,255,0.08)' }} />
      <div className="orbit-ring hidden lg:block" style={{ width: '500px', height: '500px', bottom: '-150px', left: '-150px', '--orbit-duration': '35s', borderColor: 'rgba(255,0,60,0.06)' }} />

      {/* Floating orbital dots */}
      <div className="orbit-dot hidden lg:block" style={{ top: '50%', left: '50%', '--orbit-radius': '300px', '--orbit-duration': '25s' }} />
      <div className="orbit-dot hidden lg:block" style={{ top: '30%', left: '70%', '--orbit-radius': '150px', '--orbit-duration': '15s', background: '#B026FF', boxShadow: '0 0 10px rgba(176,38,255,0.8), 0 0 30px rgba(176,38,255,0.4)' }} />

      <Navbar />

      <main className="flex-1 relative z-10 w-full flex flex-col">
        <Outlet />
      </main>

      <footer className="glass-panel border-t border-white/10 text-slate-400 py-10 relative mt-auto z-10">
        {/* Footer grid background */}
        <div className="absolute inset-0 neural-grid-bg pointer-events-none" />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <h3 className="text-xl font-bold mb-3 tracking-widest uppercase flex items-center">
                <span className="font-orbitron text-white">JOB</span>
                <span className="font-orbitron text-neon-cyan drop-shadow-[0_0_8px_rgba(0,240,255,0.8)]">PORTAL</span>
                <span className="ml-3 w-2 h-2 rounded-full bg-neon-green animate-pulse shadow-[0_0_10px_rgba(0,255,102,0.8)]" />
              </h3>
              <p className="text-sm text-slate-500 max-w-xs leading-relaxed">Find your dream job or the perfect candidate. Built with Spring Boot & Next-Gen Space Tech React UI.</p>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-4 tracking-wider uppercase text-sm font-orbitron">Quick Links</h4>
              <ul className="space-y-3 text-sm">
                <li><a href="/jobs" className="hover:text-neon-cyan transition-colors flex items-center group"><span className="w-1.5 h-1.5 rounded-full bg-neon-cyan opacity-0 group-hover:opacity-100 mr-2 transition-all shadow-[0_0_5px_rgba(0,240,255,0.8)]" />Browse Jobs</a></li>
                <li><a href="/register" className="hover:text-neon-cyan transition-colors flex items-center group"><span className="w-1.5 h-1.5 rounded-full bg-neon-cyan opacity-0 group-hover:opacity-100 mr-2 transition-all shadow-[0_0_5px_rgba(0,240,255,0.8)]" />Create Account</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-4 tracking-wider uppercase text-sm font-orbitron">Signal Hub</h4>
              <div className="group inline-flex items-center space-x-2">
                <div className="w-8 h-8 rounded-full bg-white/5 border border-white/10 flex items-center justify-center group-hover:border-neon-cyan group-hover:shadow-[0_0_10px_rgba(0,240,255,0.4)] transition-all">
                  <span className="text-neon-cyan text-sm">@</span>
                </div>
                <p className="text-sm group-hover:text-slate-300 transition-colors">support@jobportal.com</p>
              </div>
            </div>
          </div>
          <div className="border-t border-white/10 mt-10 pt-8 text-center text-sm text-slate-500 font-mono">
            <p>&copy; {new Date().getFullYear()} <span className="text-neon-cyan">JobPortal</span> // All systems operational. Latency: <span className="text-neon-green animate-pulse">12ms</span></p>
          </div>
        </div>
      </footer>
    </div>
  );
}
