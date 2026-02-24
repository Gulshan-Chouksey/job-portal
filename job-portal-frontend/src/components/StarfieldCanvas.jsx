import { useRef, useEffect } from 'react';

export default function StarfieldCanvas() {
    const canvasRef = useRef(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext('2d');
        let animId;
        let mouse = { x: 0, y: 0 };
        let w, h;

        const resize = () => {
            w = canvas.width = window.innerWidth;
            h = canvas.height = window.innerHeight;
        };
        resize();
        window.addEventListener('resize', resize);

        // Stars
        const STAR_COUNT = 600;
        const stars = Array.from({ length: STAR_COUNT }, () => ({
            x: Math.random() * 2000 - 500,
            y: Math.random() * 2000 - 500,
            z: Math.random() * 3 + 0.2,
            size: Math.random() * 2 + 0.3,
            twinkle: Math.random() * Math.PI * 2,
            twinkleSpeed: 0.01 + Math.random() * 0.03,
            color: ['#ffffff', '#00F0FF', '#B026FF', '#FF003C', '#aaccff', '#ffeedd'][Math.floor(Math.random() * 6)],
        }));

        // Shooting stars
        const shootingStars = [];
        const spawnShootingStar = () => {
            if (shootingStars.length > 3) return;
            shootingStars.push({
                x: Math.random() * w,
                y: Math.random() * h * 0.4,
                vx: (3 + Math.random() * 5) * (Math.random() > 0.5 ? 1 : -1),
                vy: 2 + Math.random() * 4,
                life: 1,
                decay: 0.008 + Math.random() * 0.01,
                length: 60 + Math.random() * 80,
            });
        };

        // Nebula clouds
        const nebulae = Array.from({ length: 5 }, () => ({
            x: Math.random() * 2000 - 500,
            y: Math.random() * 2000 - 500,
            r: 150 + Math.random() * 250,
            color: [
                'rgba(0, 240, 255, 0.02)',
                'rgba(176, 38, 255, 0.025)',
                'rgba(255, 0, 60, 0.015)',
                'rgba(0, 255, 102, 0.015)',
            ][Math.floor(Math.random() * 4)],
            drift: Math.random() * 0.1,
            angle: Math.random() * Math.PI * 2,
        }));

        const handleMouse = (e) => {
            mouse.x = (e.clientX / w - 0.5) * 2;
            mouse.y = (e.clientY / h - 0.5) * 2;
        };
        window.addEventListener('mousemove', handleMouse);

        let frame = 0;
        const draw = () => {
            ctx.clearRect(0, 0, w, h);
            frame++;

            // Nebulae
            nebulae.forEach((n) => {
                n.angle += n.drift * 0.005;
                const px = n.x - mouse.x * 15 + Math.sin(n.angle) * 30;
                const py = n.y - mouse.y * 15 + Math.cos(n.angle) * 20;
                const grad = ctx.createRadialGradient(px, py, 0, px, py, n.r);
                grad.addColorStop(0, n.color);
                grad.addColorStop(1, 'transparent');
                ctx.fillStyle = grad;
                ctx.fillRect(px - n.r, py - n.r, n.r * 2, n.r * 2);
            });

            // Stars
            stars.forEach((s) => {
                s.twinkle += s.twinkleSpeed;
                const parallaxX = mouse.x * s.z * 20;
                const parallaxY = mouse.y * s.z * 20;
                const px = s.x + parallaxX;
                const py = s.y + parallaxY;

                if (px < -50 || px > w + 50 || py < -50 || py > h + 50) return;

                const alpha = 0.3 + Math.sin(s.twinkle) * 0.4 + 0.3;
                const size = s.size * (0.8 + Math.sin(s.twinkle * 0.7) * 0.2);

                ctx.beginPath();
                ctx.arc(px, py, size, 0, Math.PI * 2);
                ctx.fillStyle = s.color;
                ctx.globalAlpha = alpha;
                ctx.fill();

                // Glow for brighter stars
                if (s.size > 1.5) {
                    ctx.beginPath();
                    ctx.arc(px, py, size * 3, 0, Math.PI * 2);
                    ctx.fillStyle = s.color;
                    ctx.globalAlpha = alpha * 0.1;
                    ctx.fill();
                }
                ctx.globalAlpha = 1;
            });

            // Shooting stars
            if (frame % 120 === 0) spawnShootingStar();
            for (let i = shootingStars.length - 1; i >= 0; i--) {
                const ss = shootingStars[i];
                ss.x += ss.vx;
                ss.y += ss.vy;
                ss.life -= ss.decay;

                if (ss.life <= 0) {
                    shootingStars.splice(i, 1);
                    continue;
                }

                const tailX = ss.x - ss.vx * (ss.length / 5);
                const tailY = ss.y - ss.vy * (ss.length / 5);

                const grad = ctx.createLinearGradient(tailX, tailY, ss.x, ss.y);
                grad.addColorStop(0, 'rgba(255,255,255,0)');
                grad.addColorStop(0.7, `rgba(0,240,255,${ss.life * 0.5})`);
                grad.addColorStop(1, `rgba(255,255,255,${ss.life})`);

                ctx.beginPath();
                ctx.moveTo(tailX, tailY);
                ctx.lineTo(ss.x, ss.y);
                ctx.strokeStyle = grad;
                ctx.lineWidth = 2;
                ctx.stroke();

                // Head glow
                ctx.beginPath();
                ctx.arc(ss.x, ss.y, 3, 0, Math.PI * 2);
                ctx.fillStyle = `rgba(255,255,255,${ss.life})`;
                ctx.fill();
            }

            animId = requestAnimationFrame(draw);
        };
        draw();

        return () => {
            cancelAnimationFrame(animId);
            window.removeEventListener('resize', resize);
            window.removeEventListener('mousemove', handleMouse);
        };
    }, []);

    return (
        <canvas
            ref={canvasRef}
            className="fixed inset-0 z-0 pointer-events-none"
            style={{ mixBlendMode: 'screen' }}
        />
    );
}
