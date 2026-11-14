/*
    SPDX-License-Identifier: AGPL-3.0-or-later
    SPDX-FileCopyrightText: 2026 Shomy
*/
use ratatui::style::Color;
use crate::themes::Theme;

/// A terminal-friendly take on Liquid Glass: deep translucent-looking surfaces,
/// cool refraction accents, and high-contrast text without unsupported blur effects.
pub const fn liquid_glass() -> Theme {
    Theme {
        name: "Liquid Glass",
        id: "liquid_glass",
        is_dark: true,
        // Deep blue-black canvas
        background: Color::Rgb(7, 12, 27),
        // Frosted surface
        foreground: Color::Rgb(22, 31, 53),
        // Luminous glass edge
        highlight: Color::Rgb(75, 139, 176),
        text: Color::Rgb(229, 239, 250),
        // Cool aqua refracted through violet
        accent: Color::Rgb(137, 220, 235),
        error: Color::Rgb(255, 126, 156),
        warning: Color::Rgb(255, 211, 137),
        info: Color::Rgb(175, 195, 255),
        success: Color::Rgb(143, 226, 202),
        muted: Color::Rgb(125, 148, 177),
    }
}
