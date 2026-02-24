import { useRef, useState } from 'react';

export default function HolographicCard({ children, className = '', as: Tag = 'div', glowColor = 'rgba(0,240,255,0.4)', ...props }) {
    const cardRef = useRef(null);
    const [style, setStyle] = useState({});
    const [isHovered, setIsHovered] = useState(false);

    const handleMouseMove = (e) => {
        if (!cardRef.current) return;
        const rect = cardRef.current.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const centerX = rect.width / 2;
        const centerY = rect.height / 2;
        const rotateX = ((y - centerY) / centerY) * -8;
        const rotateY = ((x - centerX) / centerX) * 8;

        setStyle({
            transform: `perspective(800px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.03, 1.03, 1.03)`,
            '--mouse-x': `${(x / rect.width) * 100}%`,
            '--mouse-y': `${(y / rect.height) * 100}%`,
            '--glow-color': glowColor,
        });
    };

    const handleMouseLeave = () => {
        setIsHovered(false);
        setStyle({
            transform: 'perspective(800px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)',
            transition: 'transform 0.6s cubic-bezier(0.23, 1, 0.32, 1)',
        });
    };

    const handleMouseEnter = () => {
        setIsHovered(true);
    };

    return (
        <Tag
            ref={cardRef}
            className={`holo-card ${isHovered ? 'holo-card--active' : ''} ${className}`}
            style={{ ...style, willChange: 'transform' }}
            onMouseMove={handleMouseMove}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
            {...props}
        >
            {/* Holographic refraction overlay */}
            <div
                className="holo-card__refraction"
                style={{
                    background: isHovered
                        ? `radial-gradient(circle at var(--mouse-x, 50%) var(--mouse-y, 50%), rgba(0,240,255,0.12), rgba(176,38,255,0.08), rgba(255,0,60,0.06), transparent 70%)`
                        : 'none',
                }}
            />
            {/* Scan line */}
            {isHovered && <div className="holo-card__scanline" />}
            {/* Content */}
            <div className="relative z-10">{children}</div>
        </Tag>
    );
}
