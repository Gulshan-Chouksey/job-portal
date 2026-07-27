import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="min-h-[60vh] flex flex-col items-center justify-center text-center px-4 relative z-10 overflow-hidden">
      {/* Warp tunnel background */}
      <div className="absolute inset-0 pointer-events-none">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px]">
          {[...Array(6)].map((_, i) => (
            <div key={i}
              className="absolute inset-0 border border-neon-magenta/10 rounded-full"
              style={{
                inset: `${i * 40}px`,
                animation: `orbital-spin ${8 + i * 3}s linear infinite${i % 2 === 0 ? '' : ' reverse'}`,
                borderStyle: i % 2 === 0 ? 'solid' : 'dashed',
                borderColor: i % 3 === 0 ? 'rgba(255,0,60,0.1)' : i % 3 === 1 ? 'rgba(176,38,255,0.08)' : 'rgba(0,240,255,0.06)',
              }}
            />
          ))}
        </div>
      </div>

      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-neon-magenta/10 rounded-full blur-[100px] pointer-events-none" />

      <div className="glass-panel p-16 rounded-3xl border border-neon-magenta/20 relative z-10 scan-line-overlay">
        {/* Floating astronaut */}
        <div className="text-6xl mb-6 animate-float-astro inline-block">🧑‍🚀</div>

        {/* Glitch 404 */}
        <h1 className="text-8xl md:text-9xl font-extrabold font-orbitron glitch-text tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-neon-magenta to-neon-purple">
          404
        </h1>

        <h2 className="text-2xl md:text-3xl font-bold mt-6 text-white uppercase tracking-widest font-orbitron animate-flicker">
          Sector Not Found
        </h2>
        <p className="text-slate-400 mt-4 max-w-sm mx-auto font-light leading-relaxed">
          The requested coordinate is empty or the node has been scrubbed from the network.
        </p>
        <div className="mt-4 text-xs text-slate-600 font-mono">
          ERR::COORDINATE_NULL // STATUS:404 // NODE:DESTROYED
        </div>
        <Link to="/"
          className="mt-10 inline-block px-8 py-4 bg-neon-cyan/10 border border-neon-cyan text-neon-cyan font-bold text-lg rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all duration-300 uppercase tracking-wider hover:shadow-[0_0_30px_rgba(0,240,255,0.6)] font-orbitron text-sm warp-btn">
          Return to Base
        </Link>
      </div>
    </div>
  );
}
